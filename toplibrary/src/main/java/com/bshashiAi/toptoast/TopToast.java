package com.bshashiAi.toptoast;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public class TopToast {
    
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;
    
    private Context context;
    private String message;
    private ToastType type;
    private int duration;
    private int gravity;
    private int xOffset;
    private int yOffset;
    private Drawable customIcon;
    private Integer textColor;
    private Integer backgroundColor;
    private boolean showIcon;
    private Typeface typeface;
    private float textSize;
    private AnimationType animationType;
    private boolean cancelable;
    private View customView;
    private OnToastClickListener clickListener;
    
    private Toast toast;
    private View toastView;
    
    public interface OnToastClickListener {
        void onClick(TopToast toast);
    }
    
    private TopToast(Builder builder) {
        this.context = builder.context;
        this.message = builder.message;
        this.type = builder.type;
        this.duration = builder.duration;
        this.gravity = builder.gravity;
        this.xOffset = builder.xOffset;
        this.yOffset = builder.yOffset;
        this.customIcon = builder.customIcon;
        this.textColor = builder.textColor;
        this.backgroundColor = builder.backgroundColor;
        this.showIcon = builder.showIcon;
        this.typeface = builder.typeface;
        this.textSize = builder.textSize;
        this.animationType = builder.animationType;
        this.cancelable = builder.cancelable;
        this.customView = builder.customView;
        this.clickListener = builder.clickListener;
    }
    
    public void show() {
        if (context == null) return;
        
        if (customView != null) {
            showCustomToast();
        } else {
            showStandardToast();
        }
    }
    
    private void showStandardToast() {
        LayoutInflater inflater = LayoutInflater.from(context);
        toastView = inflater.inflate(R.layout.layout_top_toast, null);
        
        // Setup views
        ViewGroup container = toastView.findViewById(R.id.toast_container);
        ImageView iconView = toastView.findViewById(R.id.toast_icon);
        TextView messageView = toastView.findViewById(R.id.toast_message);
        
        // Set message
        messageView.setText(message);
        
        // Set text color
        if (textColor != null) {
            messageView.setTextColor(textColor);
        } else {
            messageView.setTextColor(ContextCompat.getColor(context, R.color.toast_text_default));
        }
        
        // Set text size
        if (textSize > 0) {
            messageView.setTextSize(textSize);
        }
        
        // Set typeface
        if (typeface != null) {
            messageView.setTypeface(typeface);
        }
        
        // Set background based on type
        int bgResId = getBackgroundResource();
        container.setBackgroundResource(bgResId);
        
        // Set icon
        if (showIcon) {
            iconView.setVisibility(View.VISIBLE);
            if (customIcon != null) {
                iconView.setImageDrawable(customIcon);
            } else {
                iconView.setImageResource(getIconResource());
            }
        } else {
            iconView.setVisibility(View.GONE);
        }
        
        // Apply custom background color if set
        if (backgroundColor != null) {
            container.setBackgroundColor(backgroundColor);
        }
        
        // Setup click listener
        if (clickListener != null) {
            toastView.setOnClickListener(v -> clickListener.onClick(this));
        }
        
        // Create and show toast
        toast = new Toast(context);
        toast.setDuration(duration);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.setView(toastView);
        
        // Apply animation
        if (animationType != AnimationType.NONE && context instanceof Activity) {
            applyAnimation();
        }
        
        toast.show();
    }
    
    private void showCustomToast() {
        toast = new Toast(context);
        toast.setDuration(duration);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.setView(customView);
        toast.show();
    }
    
    private void applyAnimation() {
        Animation animation = null;
        switch (animationType) {
            case SLIDE_DOWN:
                animation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
                break;
            case SLIDE_UP:
                animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
                break;
            case FADE_IN:
                animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                break;
            case BOUNCE:
                animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
                break;
        }
        if (animation != null) {
            toastView.startAnimation(animation);
        }
    }
    
    private int getBackgroundResource() {
        switch (type) {
            case SUCCESS:
                return R.drawable.bg_toast_success;
            case ERROR:
                return R.drawable.bg_toast_error;
            case WARNING:
                return R.drawable.bg_toast_warning;
            case INFO:
                return R.drawable.bg_toast_info;
            case NORMAL:
            default:
                return R.drawable.bg_toast_normal;
        }
    }
    
    private int getIconResource() {
        switch (type) {
            case SUCCESS:
                return R.drawable.ic_success;
            case ERROR:
                return R.drawable.ic_error;
            case WARNING:
                return R.drawable.ic_warning;
            case INFO:
                return R.drawable.ic_info;
            case NORMAL:
            default:
                return R.drawable.ic_normal;
        }
    }
    
    public void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }
    
    // Pre-built methods for quick use
    public static void success(@NonNull Context context, @NonNull String message) {
        new Builder(context, message, ToastType.SUCCESS).show();
    }
    
    public static void error(@NonNull Context context, @NonNull String message) {
        new Builder(context, message, ToastType.ERROR).show();
    }
    
    public static void warning(@NonNull Context context, @NonNull String message) {
        new Builder(context, message, ToastType.WARNING).show();
    }
    
    public static void info(@NonNull Context context, @NonNull String message) {
        new Builder(context, message, ToastType.INFO).show();
    }
    
    public static void normal(@NonNull Context context, @NonNull String message) {
        new Builder(context, message, ToastType.NORMAL).show();
    }
    
    // Builder Class
    public static class Builder {
        private Context context;
        private String message;
        private ToastType type = ToastType.NORMAL;
        private int duration = LENGTH_SHORT;
        private int gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        private int xOffset = 0;
        private int yOffset = 50;
        private Drawable customIcon;
        private Integer textColor;
        private Integer backgroundColor;
        private boolean showIcon = true;
        private Typeface typeface;
        private float textSize = 0;
        private AnimationType animationType = AnimationType.SLIDE_DOWN;
        private boolean cancelable = true;
        private View customView;
        private OnToastClickListener clickListener;
        
        public Builder(@NonNull Context context, @NonNull String message) {
            this.context = context;
            this.message = message;
        }
        
        public Builder(@NonNull Context context, @NonNull String message, ToastType type) {
            this.context = context;
            this.message = message;
            this.type = type;
        }
        
        public Builder(@NonNull Context context, @StringRes int messageResId) {
            this.context = context;
            this.message = context.getString(messageResId);
        }
        
        public Builder type(ToastType type) {
            this.type = type;
            return this;
        }
        
        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }
        
        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }
        
        public Builder offset(int x, int y) {
            this.xOffset = x;
            this.yOffset = y;
            return this;
        }
        
        public Builder icon(@DrawableRes int iconResId) {
            this.customIcon = ContextCompat.getDrawable(context, iconResId);
            return this;
        }
        
        public Builder icon(Drawable icon) {
            this.customIcon = icon;
            return this;
        }
        
        public Builder textColor(@ColorInt int color) {
            this.textColor = color;
            return this;
        }
        
        public Builder textColorRes(@ColorRes int colorRes) {
            this.textColor = ContextCompat.getColor(context, colorRes);
            return this;
        }
        
        public Builder backgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }
        
        public Builder backgroundColorRes(@ColorRes int colorRes) {
            this.backgroundColor = ContextCompat.getColor(context, colorRes);
            return this;
        }
        
        public Builder showIcon(boolean show) {
            this.showIcon = show;
            return this;
        }
        
        public Builder typeface(Typeface tf) {
            this.typeface = tf;
            return this;
        }
        
        public Builder typeface(@FontRes int fontResId) {
            this.typeface = ResourcesCompat.getFont(context, fontResId);
            return this;
        }
        
        public Builder textSize(float size) {
            this.textSize = size;
            return this;
        }
        
        public Builder animation(AnimationType animType) {
            this.animationType = animType;
            return this;
        }
        
        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }
        
        public Builder customView(View view) {
            this.customView = view;
            return this;
        }
        
        public Builder onClick(OnToastClickListener listener) {
            this.clickListener = listener;
            return this;
        }
        
        public TopToast build() {
            return new TopToast(this);
        }
        
        public void show() {
            build().show();
        }
    }
}
