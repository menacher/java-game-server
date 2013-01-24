(function (jet) {
    "use strict";
    
    var evts = new Uint8Array(128);
        // do initialization
        for (var i = 0; i < evts.length; i++) {
            evts[i] = i;
        }
        
    // Event code Constants
    jet.ANY = evts[0];
    jet.PROTOCOL_VERSION = evts[1];

    jet.CONNECT = evts[2];
    jet.CONNECT_FAILED = evts[6];
    jet.LOG_IN = evts[8];
    jet.LOG_OUT = evts[10];
    jet.LOG_IN_SUCCESS = evts[11];
    jet.LOG_IN_FAILURE = evts[12];
    jet.LOG_OUT_SUCCESS = evts[14];
    jet.LOG_OUT_FAILURE = evts[15];

    jet.GAME_LIST = evts[16];
    jet.ROOM_LIST = evts[18];
    jet.GAME_ROOM_JOIN = evts[20];
    jet.GAME_ROOM_LEAVE = evts[22];
    jet.GAME_ROOM_JOIN_SUCCESS = evts[24];
    jet.GAME_ROOM_JOIN_FAILURE = evts[25];

    jet.START = evts[26];
    jet.STOP = evts[27];
    jet.SESSION_MESSAGE = evts[28];
    jet.NETWORK_MESSAGE = evts[29];
    jet.CHANGE_ATTRIBUTE = evts[32];
    jet.DISCONNECT = evts[34];// Use this one for handling close event of ws.
    jet.EXCEPTION = evts[36];
        
    // Functions
    // Creates a new event object
    jet.nevent = function (eventType, payload, date){
        return {
            type : eventType,
            source : payload,
            timeStamp : (typeof date === 'undefined') ? new Date().getTime() : date.getTime()
        };
    };

    // Creates a login event object to login to remote jetserver
    jet.LoginEvent = function (config){
        return jet.nevent(jet.LOG_IN,[config.user,config.pass,config.connectionKey]);
    };
    
    // If using a differnt protocol, then use this codec chain, to decode and encode incoming and outgoing requests. Something like a Chain of Responsibility pattern.
    jet.CodecChain = function (){
        var me = this;
        me.chain = [];
        for(var i = 0; i < arguments.length; i++) {
            me.chain.push(arguments[i]);
        }
    
        me.add = function (func){
            me.chain.push(func);
        };
    
        me.remove = function (func){
            var index = me.chain.indexOf(func);
            while(index != -1){
                me.chain.splice(index,1);
                index = me.chain.indexOf(func);
            }
        };
    
        me.tranform = function transform(message){
            for(var i = 0; i < me.chain.length(); i++){
                message = me.chain[i].transform(message);
            }
            return message;
        };
    };
    
    // Default codes which use JSON to decode and encode messages.
    jet.Codecs = {
        encoder : {transform: function (e){ return JSON.stringify(e)}},
        decoder : {transform: function (e){
                        var evt = JSON.parse(e);
                        if(evt.type != 'undefined' && evt.type == jet.NETWORK_MESSAGE){
                            evt.type = jet.SESSION_MESSAGE;
                        }
                        return evt;
                    }
                  }
    };
    
    // Generally a client needs only one session to the server. But this function can be used to create more.
    jet.sessionFactory = function (url,config,callback){
        new Session(url,config,callback);
    };
    
    // Used to create a session. Once START event is received from the remote jetserver then the callback is invoked with the created session.
    function Session(url, config, callback){
        var me = this;
        var callbacks = {};
        me.inCodecChain = (typeof config.inCodecChain === 'undefined') ? jet.Codecs.decoder : config.inCodecChain;
        me.outCodecChain = (typeof config.outCodecChain === 'undefined') ? jet.Codecs.encoder : config.outCodecChain;
        var message = getLogin(config);
        var ws = new WebSocket(url);
        var state = 0;// 0=CONNECTING, 1=CONNECTED,2=CLOSED
        
        ws.onopen = function(){
            ws.send(message);
        };
    
        // Login to jetserver when the start event is received the callback will return the session.
        ws.onmessage = function (e){
            var loginDecoder = (typeof config.loginDecoder === 'undefined') ? jet.Codecs.decoder : config.loginDecoder;
            var evt = loginDecoder.transform(e.data);
            if(!evt.type){
                throw new Error("Event object missing 'type' property.");
            }
            if(evt.type == jet.LOG_IN_FAILURE || evt.type == jet.GAME_ROOM_JOIN_FAILURE){
                ws.close();
            }
            if(evt.type == jet.START){
                if (callback && typeof(callback) === 'function') {
                    state = 1;
                    applyProtocol(config);
                    callback(me);
                }
            }
        };
        
        ws.onclose = function (e){
            state = 2;  
            dispatch(jet.DISCONNECT,e);
        };
        
        ws.onerror = function (e){
            state = 2;
            dispatch(jet.EXCEPTION,e);
        };
        
        me.addHandler = function(eventName, callback){
            callbacks[eventName] = callbacks[eventName] || [];
            callbacks[eventName].push(callback);
            return me;// chainable
        };
        
        me.send = function(evt){
            if(state != 1){
               throw new Error("Session is not in connected state"); 
            }
            var payload = me.outCodecChain.transform(evt);
            ws.send( payload ); // <= send JSON/Binary data to socket server
            return me;
        };
      
        me.removeHandler = function(eventName, handler){
            var handlers = callbacks[eventName];
            if (handlers instanceof Array){
                var index = handlers.indexOf(handler);
                while(index != -1){
                    handlers.splice(index,1);
                    index = handlers.indexOf(handler);
                }
            }
        };
        
        me.clearHandlers = function (){
            callbacks = {};
        };
        
        me.close = function (){
            state = 2;
            // TODO had a close and error dispatch event also.
            ws.close();  
        };
        
        function dispatch(eventName, evt){
            dispatchToEventHandlers(callback[jet.ANY], evt);
            dispatchToEventHandlers(callbacks[eventName], evt);
        }
        
        function dispatchToEventHandlers(chain, evt){
            if(typeof chain === 'undefined') return; // no callbacks for this event
            for(var i = 0; i < chain.length; i++){
              chain[i]( evt );
            }
        }
        
        function getLogin(config){
            var loginEvent = jet.LoginEvent(config);
            var loginEncoder = (typeof config.loginEncoder === 'undefined') ? jet.Codecs.encoder : config.loginEncoder;
            return loginEncoder.transform(loginEvent);
        }
        
        function applyProtocol(config){
            var func = (typeof config.protocol === 'undefined') ? protocol : config.protocol;
            ws.onmessage = func;
        }
        
        function protocol(e){
            var evt = me.inCodecChain.transform(e.data);
            dispatch(evt.type, evt);
        }
    }
}( window.jet = window.jet || {}));