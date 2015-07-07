package com.nepotech.practicalanswers.home_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.items.ItemsDBHelper;
import com.nepotech.practicalanswers.items.SingleItemActivity;
import com.nepotech.practicalanswers.our_resources_activity.OurResourcesActivity;

import java.util.ArrayList;
import java.util.Random;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    Context mContext;
    ArrayList<HomeActivity.HomeRecyclerItem> mContent;

    private final int BANNER = HomeActivity.HomeRecyclerItem.BANNER;
    private final int HEADER = HomeActivity.HomeRecyclerItem.HEADER;
    private final int ITEM_CARD = HomeActivity.HomeRecyclerItem.ITEM_CARD;

    public HomeRecyclerAdapter(Context context, ArrayList<HomeActivity.HomeRecyclerItem> content) {
        mContext = context;
        mContent = content;
    }

    public void updateContent(ArrayList<HomeActivity.HomeRecyclerItem> content) {
        mContent = content;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mContent.get(position).viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case BANNER:
                v = inflater.inflate(R.layout.home_rv_banner, parent, false);
                break;
            case HEADER:
                v = inflater.inflate(R.layout.home_rv_header, parent, false);
                break;
            case ITEM_CARD:
                v = inflater.inflate(R.layout.home_rv_item, parent, false);
                break;
            default:
                throw new IllegalStateException("Unexpected viewType (= " + viewType + ")");
        }
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HomeActivity.HomeRecyclerItem recyclerItem = mContent.get(position);
        switch (recyclerItem.viewType) {
            case BANNER:
                onBindBanner(holder, position, recyclerItem);
                break;
            case HEADER:
                onBindHeader(holder, position, recyclerItem);
                break;
            case ITEM_CARD:
                onBindItem(holder, position, recyclerItem);
                break;
            default:
                break;
        }
    }


    private void onBindBanner(ViewHolder holder, int position, final HomeActivity.HomeRecyclerItem item) {
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OurResourcesActivity.class);
                mContext.startActivity(intent);
            }
        });
        int images[] = {
                R.drawable.welcome1,
                R.drawable.welcome2,
                R.drawable.welcome3};
        Random random = new Random();
        int randInt = random.nextInt(3);
        ((ImageView) holder.itemView.findViewById(R.id.image_view)).setImageResource(images[randInt]);
    }

    private void onBindHeader(ViewHolder holder, int position, final HomeActivity.HomeRecyclerItem item) {
        holder.textView.setText(item.text);
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(item.intent);
            }
        });
        if (item.visible)
            holder.moreButton.setVisibility(View.VISIBLE);
        else
            holder.moreButton.setVisibility(View.INVISIBLE);

    }


    private void onBindItem(final ViewHolder holder, int position, final HomeActivity.HomeRecyclerItem item) {
        if (item.visible) {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.textView.setText(item.text);
            holder.textView.setMaxLines(3);
            holder.textView.setEllipsize(TextUtils.TruncateAt.END);

            // set document thumb imageview
            Uri uri = Uri.parse(item.imageHref);

            ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(
                        String id,
                        @Nullable ImageInfo imageInfo,
                        @Nullable Animatable anim) {
                    if (imageInfo == null) {
                        return;
                    }
                    holder.draweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                }

                @Override
                public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                    if (imageInfo == null) {
                        return;
                    }
                    holder.draweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    FLog.e(getClass(), throwable, "Error loading %s", id);
                }
            };
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setControllerListener(controllerListener)
                    .setUri(uri)
                    .build();
            holder.draweeView.setController(controller);

            CardView cv = (CardView) holder.itemView.findViewById(R.id.card_view);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(cv.getLayoutParams());
            DisplayMetrics dm = cv.getResources().getDisplayMetrics();
            switch (item.col) {
                case 1:
                    lp.setMargins(dp_px(8, dm), dp_px(4, dm), dp_px(4, dm), dp_px(4, dm));
                    break;
                case 2:
                    lp.setMargins(dp_px(4, dm), dp_px(4, dm), dp_px(4, dm), dp_px(4, dm));
                    break;
                case 3:
                    lp.setMargins(dp_px(4, dm), dp_px(4, dm), dp_px(8, dm), dp_px(4, dm));
                    break;
            }
            cv.setLayoutParams(lp);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SingleItemActivity.class);
                    intent.putExtra(ItemsDBHelper.COLUMN_DSPACE_ID, item.dspace_id);
                    intent.putExtra(ItemsDBHelper.COLUMN_TITLE, item.category);
                    mContext.startActivity(intent);
                }
            });

        } else
            holder.itemView.setVisibility(View.INVISIBLE);
    }

    private int dp_px(int dp, DisplayMetrics displayMetrics) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(pixels);
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button moreButton;
        View itemView;
        SimpleDraweeView draweeView;


        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            if (viewType == HEADER || viewType == BANNER)
                moreButton = (Button) itemView.findViewById(R.id.button);
            if (viewType == ITEM_CARD) {
                draweeView = (SimpleDraweeView) itemView.findViewById(R.id.image_view);
            }
            this.itemView = itemView;
        }
    }
}
