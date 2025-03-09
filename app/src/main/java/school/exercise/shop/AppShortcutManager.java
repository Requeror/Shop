package school.exercise.shop;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import java.util.Arrays;

public class AppShortcutManager {
    public static void createShortcuts(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            // Create Order List shortcut
            ShortcutInfo orderListShortcut = new ShortcutInfo.Builder(context, "id_order_list")
                    .setShortLabel(context.getString(R.string.shortcut_order_list_short))
                    .setLongLabel(context.getString(R.string.shortcut_order_list_long))
                    .setIcon(Icon.createWithResource(context, android.R.drawable.ic_menu_agenda))
                    .setIntent(new Intent(context, OrderList.class)
                            .setAction(Intent.ACTION_VIEW))
                    .build();

            // Create Send SMS shortcut
            ShortcutInfo smsShortcut = new ShortcutInfo.Builder(context, "id_send_sms")
                    .setShortLabel(context.getString(R.string.shortcut_sms_short))
                    .setLongLabel(context.getString(R.string.shortcut_sms_long))
                    .setIcon(Icon.createWithResource(context, android.R.drawable.ic_dialog_email))
                    .setIntent(new Intent(context, SendSMS.class)
                            .setAction(Intent.ACTION_VIEW))
                    .build();

            // Create Share Order shortcut
            ShortcutInfo shareShortcut = new ShortcutInfo.Builder(context, "id_share")
                    .setShortLabel(context.getString(R.string.shortcut_share_short))
                    .setLongLabel(context.getString(R.string.shortcut_share_long))
                    .setIcon(Icon.createWithResource(context, android.R.drawable.ic_menu_share))
                    .setIntent(new Intent(context, MainActivity.class)
                            .setAction("school.exercise.shop.SHARE_ORDER"))
                    .build();

            // Create Calculate Price shortcut
            ShortcutInfo priceShortcut = new ShortcutInfo.Builder(context, "id_price")
                    .setShortLabel(context.getString(R.string.shortcut_price_short))
                    .setLongLabel(context.getString(R.string.shortcut_price_long))
                    .setIcon(Icon.createWithResource(context, android.R.drawable.ic_menu_info_details))
                    .setIntent(new Intent(context, MainActivity.class)
                            .setAction("school.exercise.shop.CALCULATE_PRICE"))
                    .build();

            if (shortcutManager != null) {
                shortcutManager.setDynamicShortcuts(Arrays.asList(
                        orderListShortcut,
                        smsShortcut,
                        shareShortcut,
                        priceShortcut
                ));
            }
        }
    }
} 