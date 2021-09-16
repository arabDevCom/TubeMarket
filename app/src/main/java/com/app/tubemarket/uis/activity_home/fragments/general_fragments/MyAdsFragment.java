package com.app.tubemarket.uis.activity_home.fragments.general_fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tubemarket.R;
import com.app.tubemarket.adapters.CampaignAdapter;
import com.app.tubemarket.adapters.MyAdsAdapter;
import com.app.tubemarket.databinding.FragmentCampaignBinding;
import com.app.tubemarket.databinding.FragmentMyAdsBinding;
import com.app.tubemarket.interfaces.Listeners;
import com.app.tubemarket.models.CampaignDataModel;
import com.app.tubemarket.models.CampaignModel;
import com.app.tubemarket.models.MyAdsDataModel;
import com.app.tubemarket.models.MyAdsModel;
import com.app.tubemarket.models.StatusResponse;
import com.app.tubemarket.models.UserModel;
import com.app.tubemarket.preferences.Preferences;
import com.app.tubemarket.remote.Api;
import com.app.tubemarket.share.Common;
import com.app.tubemarket.tags.Tags;
import com.app.tubemarket.uis.activity_home.HomeActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyAdsFragment extends Fragment implements Listeners.MyAdsListener {

    private FragmentMyAdsBinding binding;
    private HomeActivity activity;
    private UserModel userModel;
    private Preferences preferences;
    private int type;
    private List<MyAdsModel> list;
    private MyAdsAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_ads, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        list = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);

        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new MyAdsAdapter(activity, list, this);
        binding.recView.setAdapter(adapter);


        getMyAds();
    }

    private void getMyAds() {
        binding.loader.setVisibility(View.VISIBLE);
        list.clear();
        adapter.notifyDataSetChanged();
        Api.getService(Tags.base_url)
                .getMyAds("Bearer " + userModel.getToken(), userModel.getId())
                .enqueue(new Callback<MyAdsDataModel>() {
                    @Override
                    public void onResponse(Call<MyAdsDataModel> call, Response<MyAdsDataModel> response) {
                        binding.loader.setVisibility(View.GONE);
                        Log.e("code", response.body().getStatus()+"__");
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("hh", "hhh__");

                            if (response.body().getStatus() == 200) {
                                list.clear();
                                list.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();
                                Log.e("ss", list.size()+"_");


                            }
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyAdsDataModel> call, Throwable t) {
                        binding.loader.setVisibility(View.GONE);
                        Log.e("fail", t.getMessage());
                    }
                });
    }


    private void delete(MyAdsModel model) {
        ProgressDialog dialog = Common.createProgressDialog(activity, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .deleteCampaign("Bearer " + userModel.getToken(), model.getId())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                getMyAds();
                            }

                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                } else {
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    @Override
    public void onMyAdsData(MyAdsModel model, int type, View view) {
        if (type == 1) {
            /*Bundle bundle = new Bundle();
            bundle.putSerializable("data", model);
            Navigation.findNavController(root).navigate(R.id.campaignDetailsFragment, bundle);
*/
        } else if (type == 2) {
            //delete(model);
        }
    }
}