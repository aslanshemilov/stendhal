/*
 * @(#) src/games/stendhal/client/events/BuddyChangeListener.java
 *
 * $Id$
 */

package games.stendhal.client.events;

/**
 * A listener of buddy events.
 */
public interface BuddyChangeListener {
	/**
	 * A buddy was added.
	 *
	 * @param	buddyName	The name of the buddy.
	 */
	public void buddyAdded(String buddyName);

	/**
	 * A buddy went offline.
	 *
	 * @param	buddyName	The name of the buddy.
	 */
	public void buddyOffline(String buddyName);

	/**
	 * A buddy went online.
	 *
	 * @param	buddyName	The name of the buddy.
	 */
	public void buddyOnline(String buddyName);

	/**
	 * A buddy was removed.
	 *
	 * @param	buddyName	The name of the buddy.
	 */
	public void buddyRemoved(String buddyName);
}
