package games.stendhal.server.maps;

import games.stendhal.server.Path;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.*;

public class village 
  {
  public village(StendhalRPZone zone)
    {
    Sign sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(23);
    sign.sety(61);
    sign.setText("You are about to leave this area and move to the plains.|You may fatten up your sheep there on the wild berries.|Be careful though, wolves roam these plains.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(26);
    sign.sety(41);
    sign.setText("Talk to Nishiya to buy a sheep!.|He has the best prices for miles.");
    zone.add(sign);

    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(60);
    sign.sety(47);
    sign.setText("You are about to leave this area to move to the city.|You can sell your sheep there.");    
    zone.add(sign);
    
    sign=new Sign();
    zone.assignRPObjectID(sign);
    sign.setx(16);
    sign.sety(35);
    sign.setText("[CLOSED]|The tavern has moved to a much|better and central house in town.|Come buy your weapons, find your|quests and hang out there instead.");
    zone.add(sign);
    
    NPC npc=new SpeakerNPC()
      {
      protected void createPath()
        {
        List<Path.Node> nodes=new LinkedList<Path.Node>();
        nodes.add(new Path.Node(33,44));
        nodes.add(new Path.Node(33,42));
        nodes.add(new Path.Node(23,42));
        nodes.add(new Path.Node(23,44));
        setPath(nodes,true);
        }
      
      protected void createDialog()
        {        
        class SheepSellerBehaviour extends Behaviours.SellerBehaviour
          {
          SheepSellerBehaviour(Map<String,Integer> items)
            {
            super(items);
            }
            
          public boolean onSell(SpeakerNPC seller, Player player, String itemName, int itemPrice)
            {
            if(!player.hasSheep())
              {
              seller.say("Congratulations! Here is your sheep!Keep it safe!");
              StendhalRPZone zone=(StendhalRPZone)world.getRPZone(seller.getID());
              
              Sheep sheep=new Sheep(player);
              zone.assignRPObjectID(sheep);

              sheep.setx(seller.getx());
              sheep.sety(seller.gety()+2);
              
              world.add(sheep);
              
              player.setSheep(sheep);        
              world.modify(player);

              chargePlayer(player,itemPrice);
              return true;
              }
            else
              {
              say("You already have a sheep. Take care of it first!");
              return false;
              }
            }
          }
          
        Map<String,Integer> items=new HashMap<String,Integer>();
        items.put("sheep",30);
        
        Behaviours.addGreeting(this);
        Behaviours.addGoodbye(this);
        Behaviours.addSeller(this,new Behaviours.SellerBehaviour(items));
        }
      };
      
    zone.assignRPObjectID(npc);
    npc.put("class","sellernpc");
    npc.setName("Nishiya");
    npc.setx(33);
    npc.sety(44);
    npc.setBaseHP(100);
    npc.setHP(npc.getBaseHP());
    zone.add(npc);    
    zone.addNPC(npc);

//    SellerNPC npc=new SellerNPC()
//      {
//      protected void createPath()
//        {
//        List<Path.Node> nodes=new LinkedList<Path.Node>();
//        nodes.add(new Path.Node(33,44));
//        nodes.add(new Path.Node(33,42));
//        nodes.add(new Path.Node(23,42));
//        nodes.add(new Path.Node(23,44));
//        setPath(nodes,true);
//        }
//      };
//      
//    zone.assignRPObjectID(npc);
//    npc.setName("Nishiya");
//    npc.setx(33);
//    npc.sety(44);
//    npc.setBaseHP(100);
//    npc.setHP(npc.getBaseHP());
//    zone.add(npc);    
//    zone.addNPC(npc);
    }
  }
