package com.nepotech.practicalanswers.items;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.nepotech.practicalanswers.R;

import java.util.ArrayList;

public class ItemsRecyclerViewAdapter extends RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Item> mItems;
    String mWindowTitle;


    public ItemsRecyclerViewAdapter(Context context, ArrayList<Item> items, String windowTitle) {
        super();
        this.mItems = items;
        this.mContext = context;
        this.mWindowTitle = windowTitle;
    }

    public void updateItems (ArrayList<Item> items) {
        this.mItems = items;
    }

    @Override
    public int getItemCount() {
        if(mItems == null)
            return 0;
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.single_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Item rowItem = mItems.get(i);

        viewHolder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SingleItemActivity.class);
                intent.putExtra(ItemsDBHelper.COLUMN_DSPACE_ID, rowItem.getDspaceId());
                intent.putExtra(ItemsDBHelper.COLUMN_TITLE, mWindowTitle);
                mContext.startActivity(intent);


            }
        });
        // set textviews
        viewHolder.txtDesc.setText(rowItem.getDescription());
        viewHolder.txtTitle.setText(rowItem.getTitle());
        String imageUrl = rowItem.getDocumentThumbHref();

        // set document thumb imageview
        Uri uri = Uri.parse(imageUrl);
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                viewHolder.draweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                if (imageInfo == null) {
                    return;
                }
                viewHolder.draweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
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
        viewHolder.draweeView.setController(controller);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    // ViewHolder Class //
    public static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView draweeView;
        TextView txtTitle;
        TextView txtDesc;
        View v;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
            txtDesc = (TextView) v.findViewById(R.id.item_description);
            txtTitle = (TextView) v.findViewById(R.id.item_title);
            draweeView = (SimpleDraweeView) v.findViewById(R.id.doc_thumb);
        }
    }
}
