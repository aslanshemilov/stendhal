package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * show a list of all items for which offers exist.
 */
public class ShowOfferItemsChatAction implements ChatAction {

	/**
	 * show a list of all items for which offers exist.
	 */
	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		RPSlot offersSlot = market.getSlot(Market.OFFERS_SLOT_NAME);
		List<Offer> offers = getOffers(offersSlot);
		if (offers.isEmpty()) {
			npc.say("Sorry, there are currently no offers.");
		} else {
			String text = buildItemListText(buildItemList(offers));
			npc.say(text);
		}
	}

	/**
	 * gets a list of all offers
	 *
	 * @param slot slot to get the offers from
	 * @return list of offers
	 */
	private List<Offer> getOffers(RPSlot slot) {
		LinkedList<Offer> offers = new LinkedList<Offer>();
		for (RPObject rpObject : slot) {
			offers.add((Offer) rpObject);
		}
		return offers;
	}

	/**
	 * creates an alphabetically sorted set of items for which offers exist
	 *
	 * @param offers list of offers
	 * @return set of items
	 */
	private Set<String> buildItemList(List<Offer> offers) {
		Set<String> items = new TreeSet<String>();
		for (Offer offer : offers) {
			items.add(offer.getItem().getName());
		}
		return items;
	}

	/**
	 * creates the response text based on the item set
	 *
	 * @param items items to list
	 * @return text for the NPC to say
	 */
	private String buildItemListText(Set<String> items) {
		StringBuilder sb = new StringBuilder();
		sb.append("I have offers for the following items: ");
		boolean first = true;
		for (String item : items) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append("#'show " + item + "'");
		}
		return sb.toString();
	}
}
