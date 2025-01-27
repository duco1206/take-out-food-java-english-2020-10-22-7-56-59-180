import java.util.HashMap;
import java.util.List;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    private ItemRepository itemRepository;
    private SalesPromotionRepository salesPromotionRepository;

    public App(ItemRepository itemRepository, SalesPromotionRepository salesPromotionRepository) {
        this.itemRepository = itemRepository;
        this.salesPromotionRepository = salesPromotionRepository;
    }

    public String bestCharge(List<String> inputs) {
        String total = getTotalCost(inputs);
        String details = total.split("&")[0];
        int totalCost = (int)Double.parseDouble(total.split("&")[1]);

        String discountStr = get50_DISCOUNTCost(inputs);
        String sale50off = discountStr.split("&")[0];
        int discountCost = (int)Double.parseDouble(discountStr.split("&")[1]);

        int sale30_5Cost = 0;

        String returnStr = "============= Order details =============\n" +
                details +
                "%s-----------------------------------\n" +
                "Total:%s yuan\n" +
                "===================================";
        String sale30_5 = "-----------------------------------\n" +
                "Promotion used:\n" +
                "Deduct 6 yuan when the order reaches 30 yuan,saving 6 yuan\n";

        String promotion = "-----------------------------------\n" +
                "Promotion used:\n" +
                "Half price for certain dishes (%s),saving %s yuan\n";

        if(totalCost >= 30){
            sale30_5Cost = totalCost-6;
        }else {
            sale30_5Cost = totalCost;
        }

        if(totalCost > 0 && sale30_5Cost >0 && discountCost >0){
            if(totalCost == sale30_5Cost && sale30_5Cost == discountCost){
                return String.format(returnStr,"",totalCost);
            }else if (discountCost < sale30_5Cost){
                return String.format(returnStr,String.format(promotion,sale50off,(totalCost-discountCost)),discountCost);
            }else {
                return String.format(returnStr,sale30_5,sale30_5Cost);
            }
        }



        return null;
    }

    public List<String> getSalesPromotionRelatedItems(){
        List<SalesPromotion> salesPromotionList = salesPromotionRepository.findAll();
        for (SalesPromotion salesPromotion : salesPromotionList) {
            if (salesPromotion.getType().equals("50%_DISCOUNT_ON_SPECIFIED_ITEMS")){
                return salesPromotion.getRelatedItems();
            }
        }
        return null;
    }

    public String getTotalCost(List<String> inputs){
        List<Item> itemList = itemRepository.findAll();
        double beforePrice = 0;
        String details = "";
        //ITEM0013 x 4
        for (String inputItem:inputs){
            String[] split = inputItem.split(" x ");
            String itemid = split[0];
            int itemNum = Integer.parseInt(split[1]);
            double itemPrice = 0;
            String itemName = "";
            for (Item item : itemList) {
                if(item.getId().equals(itemid)){
                    itemPrice = item.getPrice();
                    itemName = item.getName();
                }
            }
            details = details + itemName + " x " + itemNum + " = " + (int)(itemNum*itemPrice) + " yuan\n";
            beforePrice += itemNum*itemPrice;
        }

        return details + "&" + (int)beforePrice;
    }

    public String get50_DISCOUNTCost(List<String> inputs){
        List<Item> itemList = itemRepository.findAll();
        List<String> salesPromotionRelatedItems = getSalesPromotionRelatedItems();
        double price = 0;

        String itemName = "";
        //ITEM0013 x 4
        for (String inputItem:inputs){
            String[] split = inputItem.split(" x ");
            String itemid = split[0];
            int itemNum = Integer.parseInt(split[1]);
            double itemPrice = 0;
            for (Item item : itemList) {
                if(item.getId().equals(itemid)){
                    itemPrice = item.getPrice();
                    if(salesPromotionRelatedItems.contains(itemid)){
                        itemPrice = item.getPrice()/2;
                        itemName =itemName + "," + item.getName();
                    }
                }
            }
            price += itemNum*itemPrice;
        }
        if(itemName.length()>0){
            return itemName.substring(1) +"&" + price;
        }
        return "&" + price;
    }


}
