package com.compilesense.liuyi.detectiondemo.model.bean;

import java.util.List;

/**
 * Created by wujun on 2016/10/9.
 */
public class KeyPointBean {

    /**
     * faces : [{"X":"283","Y":"587","height":"516","points":[{"X":"1","Y":"1"}],"width":"516"}]
     * status : OK
     */

    private String status;
    /**
     * X : 283
     * Y : 587
     * height : 516
     * points : [{"X":"1","Y":"1"}]
     * width : 516
     */

    private List<FacesBean> faces;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FacesBean> getFaces() {
        return faces;
    }

    public void setFaces(List<FacesBean> faces) {
        this.faces = faces;
    }

    public static class FacesBean {
        private String X;
        private String Y;
        private String height;
        private String width;
        /**
         * X : 1
         * Y : 1
         */

        private List<PointsBean> points;

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

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public List<PointsBean> getPoints() {
            return points;
        }

        public void setPoints(List<PointsBean> points) {
            this.points = points;
        }

        @Override
        public String toString() {
            return "FacesBean{" +
                    "width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    ", Y='" + Y + '\'' +
                    ", X='" + X + '\'' +
                    '}';
        }

        public static class PointsBean {
            private String X;
            private String Y;

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

            @Override
            public String toString() {
                return "PointsBean{" +
                        "X='" + X + '\'' +
                        ", Y='" + Y + '\'' +
                        '}';
            }
        }
    }
}
