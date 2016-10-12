package com.compilesense.liuyi.detectiondemo.model.bean;

/**
 * Created by wujun on 2016/10/9.
 */
public class FaceBean {


    /**
     * status : OK
     * attribute : {"X":"39","Y":"28","width":"415","height":"415"}
     */

    private String status;
    /**
     * X : 39
     * Y : 28
     * width : 415
     * height : 415
     */

    private AttributeBean attribute;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AttributeBean getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeBean attribute) {
        this.attribute = attribute;
    }

    public static class AttributeBean {
        private String X;
        private String Y;
        private String width;
        private String height;

        public String getX() {
            return X;
        }

        public void setX(String X) {
            this.X = X;
        }

        public String getY() {
            return Y;
        }

        public void setY(String Y) {
            this.Y = Y;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }
    }
}
