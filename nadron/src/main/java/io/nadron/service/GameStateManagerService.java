package io.nadron.service;

/**
 * Every multi-player game requires some sort of state management, this
 * interface contains methods which can be implemented for doing such service.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface GameStateManagerService
{
	/**
	 * @return Returns the current state object. This could be any object like a
	 *         byte array, an object reference that holds a hierarchy of other
	 *         objects etc. This will be used to manage the state of the game.
	 */
	public Object getState();
	
	/**
	 * Set's current state object. This could be any object like a byte array,
	 * an object reference that holds a hierarchy of other objects etc. This
	 * will be used to manage the state of the game.
	 */
	public void setState(Object state);

	/**
	 * Method used to achieve synchronization while doing state management.
	 * PictureDataStateManager does it by using an AtomicInteger.
	 * 
	 * @param key
	 *            The current state is wrapped in this object key.
	 * @return True if the atomic compare and set was successful. False
	 *         otherwise.
	 */
	public boolean compareAndSetSyncKey(Object key);

	/**
	 * This method is actually a combination of compareAndSetSyncKey and
	 * setState. It will take care of setting the new sync key and set state. If
	 * the sync key is not valid it will return false, if it is, it will set the
	 * state and the new sync key.
	 * 
	 * @param syncKey
	 *            Sync key of the incoming object
	 * @param state
	 *            The new state to be set.
	 * @return True if sync key is valid, false if it is invalid.
	 */
	public boolean compareAndSetState(Object syncKey, Object state);

	/**
	 * Method used to retrieve the synchronization key object. For the case of
	 * Doodler application it is an AtomicInteger.
	 * 
	 * @return Returns the synchronization key associated with the state
	 *         manager.
	 */
	public Object getSyncKey();

	/**
	 * Whenever serialization is done from Java object to AMF3, or just plain
	 * seriazlied java object, it should be saved in byte array format to the
	 * state manager. This will save re-serialization in case the same object is
	 * to be sent again. Below method will return the saved array.
	 * 
	 * @return Return the latest byte array representation of the state.
	 */
	public byte[] getSerializedByteArray();

	/**
	 * Whenever serialization is done from Java object to AMF3, or just plain
	 * java serialized object, it should be saved in byte array format to the
	 * state manager. This will save re-serialization in case the same object is
	 * to be sent again. Below method will set the array to the state manager.
	 * 
	 * @param serializedBytes
	 *            the serialized AMF3 or other object in byte array format.
	 * 
	 */
	public void setSerializedByteArray(byte[] serializedBytes)
			throws UnsupportedOperationException;

	public Object computeNextState(Object state, Object syncKey,
			Object stateAlgorithm) throws UnsupportedOperationException;

	public Object computeAndSetNextState(Object state, Object syncKey,
			Object stateAlgorithm) throws UnsupportedOperationException;

	public Object getStateAlgorithm() throws UnsupportedOperationException;
}
