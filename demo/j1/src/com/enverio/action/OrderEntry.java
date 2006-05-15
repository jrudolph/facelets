package com.enverio.action;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.component.UIData;
import javax.faces.component.UIOutput;
import javax.faces.model.SelectItem;

import com.enverio.model.Catalog;
import com.enverio.model.Company;
import com.enverio.model.Order;
import com.enverio.model.OrderLine;
import com.enverio.model.Product;
import com.enverio.model.Uom;
import com.sun.facelets.Facelets;
import com.sun.facelets.client.ClientWriter;
import com.sun.facelets.client.Effect;

public class OrderEntry implements Serializable {
    
    private final static Logger log = Logger.getLogger("demo.OrderEntry");

    public static void main(String[] argv) throws Exception {
        Company c = new Company();
        Catalog cat = c.getCatalog();
        Order order = new Order();

        OrderEntry oe = new OrderEntry();
        oe.setOrder(order);
        oe.setProduct(cat.findProduct(59339));
        oe.addProduct();
        oe.addProduct();
        oe.addProduct();

        System.out.println(order.getTotal());
    }

    private Order order;

    private Product product;

    private String uom;
    
    private int quantity = 1;
    
    private UIData data;
    
    private UIOutput total;

    public OrderEntry() {
        super();
    }

    public void addProduct() throws Exception {
        if (this.product == null) {
            this.product = (Product) this.data.getRowData();
        }
        if (this.product != null) {

            // default UOM if need be
            if (this.uom == null) {
                this.uom = this.product.getUoms().get(0).getUnits();
            }
            
            // default quantity
            if (this.quantity == 0) {
                this.quantity = 1;
            }
            
            
            log.info("Adding Product: "+this.product+" for "+this.uom+"/"+this.quantity);
            
            this.order.getLines().add(0,
                    new OrderLine(this.product, this.uom, this.quantity));
            
            if (this.total != null) {
                Facelets.encode(this.total);
                ClientWriter cw = Facelets.getClientWriter();
                cw.startScript();
                cw.select(this.total, Effect.highlight());
                cw.endScript();
            }
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public static SelectItem[] selectUom(List<Uom> list) {
        SelectItem[] si = new SelectItem[list.size()];
        Uom u;
        for (int i = 0; i < si.length; i++) {
            u = list.get(i);
            si[i] = new SelectItem(u.getUnits(), u.getUnits());
        }
        return si;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public UIData getData() {
        return data;
    }

    public void setData(UIData data) {
        this.data = data;
    }

    public UIOutput getTotal() {
        return total;
    }

    public void setTotal(UIOutput total) {
        this.total = total;
    }

}
