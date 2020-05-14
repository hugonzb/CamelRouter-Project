package creator;

import domain.Summary;

public class changeGroupToId {
    public Summary changeGroup(Summary summary) {
        Summary s = new Summary();
        s.setNumberOfSales(summary.getNumberOfSales());
        s.setTotalPayment(summary.getTotalPayment());
        if(summary.getGroup().equals("Regular Customers")){
            s.setGroup("0afa8de1-147c-11e8-edec-2b197906d816");
        } else {
            s.setGroup("0afa8de1-147c-11e8-edec-201e0f00872c");
        } 
        return s;
    }
}
