package com.example.android.utabazzar;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;
/**
 * @author S.Shahini
 * @since 2/12/18
 */

public class MainSliderAdapter extends SliderAdapter {

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                viewHolder.bindImageSlide("https://cnet2.cbsistatic.com/img/rvIz1qsTJOUomXxVIX8gv-5FjLw=/770x433/2017/06/27/13484418-bfd9-41e2-8f2d-9b4afb072da8/apple-macbook-pro-15-inch-2017-14.jpg");
                break;
            case 1:
                viewHolder.bindImageSlide("https://i5.walmartimages.ca/images/Large/313/596/6000197313596.jpg?odnBound=460");
                break;
            case 2:
                viewHolder.bindImageSlide("https://boygeniusreport.files.wordpress.com/2017/03/samsung-galaxy-s8-13.jpg?quality=98&strip=all&w=782");
                break;
        }
    }

}
