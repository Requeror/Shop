package school.exercise.shop;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class OrderList extends AppCompatActivity {
    ListView listView;
    Database db;
    ArrayList<Item> orderItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new Database(this);
        orderItems = db.getAllItems();

        listView = findViewById(R.id.customListView);

        OrderAdapter adapter = new OrderAdapter(this, orderItems);
        listView.setAdapter(adapter);
    }

    private class OrderAdapter extends ArrayAdapter<Item> {
        private Context context;
        private ArrayList<Item> items;

        public OrderAdapter(Context context, ArrayList<Item> items) {
            super(context, R.layout.order_list_item, items);
            this.context = context;
            this.items = items;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if(listItem == null) {
                listItem = LayoutInflater.from(context).inflate(R.layout.order_list_item, parent, false);
            }

            Item currentItem = items.get(position);

            ImageView imageView = listItem.findViewById(R.id.itemImage);
            TextView name = listItem.findViewById(R.id.itemName);
            TextView price = listItem.findViewById(R.id.itemPrice);
            TextView buyer = listItem.findViewById(R.id.buyerName);
            TextView date = listItem.findViewById(R.id.orderDate);

            imageView.setImageResource(currentItem.getPict());
            name.setText(currentItem.getName());
            price.setText("Price: $" + currentItem.getPrice());
            buyer.setText("Buyer: " + currentItem.getBuyer());
            date.setText("Date: " + currentItem.getDate());

            return listItem;
        }
    }
}