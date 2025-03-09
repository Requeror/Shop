package school.exercise.shop;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String name;
    private int price;
    private Bitmap image;
    private int pictureResourceId;
    private String date;
    private String buyer;

    public Item(String name, int price, int pictureResourceId, String date, String buyer) {
        this.name = name;
        this.price = price;
        this.pictureResourceId = pictureResourceId;
        this.date = date;
        this.buyer = buyer;
    }

    public Item(String name, int price, Bitmap image, String date, String buyer) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.date = date;
        this.buyer = buyer;
    }

    protected Item(Parcel in) {
        name = in.readString();
        price = in.readInt();
        pictureResourceId = in.readInt();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        date = in.readString();
        buyer = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getPict() {
        return pictureResourceId;
    }

    public void setPict(int pictureResourceId) {
        this.pictureResourceId = pictureResourceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeInt(pictureResourceId);
        dest.writeParcelable(image, flags);
        dest.writeString(date);
        dest.writeString(buyer);
    }
}
