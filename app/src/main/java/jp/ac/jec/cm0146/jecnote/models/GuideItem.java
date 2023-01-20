package jp.ac.jec.cm0146.jecnote.models;

public class GuideItem {
    private int id;
    private int orderNum;
    private String title;
    private int hit;

    public int getHit() {
        return hit;
    }

    public void setHit(String hit) {
        this.hit = Integer.parseInt(hit);
    }

    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = Integer.parseInt(orderNum);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
