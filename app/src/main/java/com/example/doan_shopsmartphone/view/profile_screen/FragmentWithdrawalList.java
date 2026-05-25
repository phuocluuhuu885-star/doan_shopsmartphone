package com.example.doan_shopsmartphone.view.profile_screen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.doan_shopsmartphone.adapter.WithdrawalAdapter;
import com.example.doan_shopsmartphone.api.BaseApi;
import com.example.doan_shopsmartphone.databinding.FragmentWithdrawalListBinding;
import com.example.doan_shopsmartphone.model.Withdrawal;
import com.example.doan_shopsmartphone.model.response.WithdrawalListResponse;
import com.example.doan_shopsmartphone.ultil.AccountUltil;
import com.example.doan_shopsmartphone.ultil.TAG;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentWithdrawalList extends Fragment implements WithdrawalAdapter.OnWithdrawalClickListener {

    private FragmentWithdrawalListBinding binding;
    private List<Withdrawal> withdrawalList;
    private WithdrawalAdapter adapter;
    private String status = "pending"; // "pending" or "processed"

    public FragmentWithdrawalList() {
        // Required empty public constructor
    }

    public static FragmentWithdrawalList newInstance(String status) {
        FragmentWithdrawalList fragment = new FragmentWithdrawalList();
        Bundle args = new Bundle();
        args.putString("STATUS_KEY", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString("STATUS_KEY", "pending");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWithdrawalListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initView() {
        withdrawalList = new ArrayList<>();
        adapter = new WithdrawalAdapter(requireContext(), withdrawalList, this);
        binding.rcvWithdrawal.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rcvWithdrawal.setAdapter(adapter);
    }

    public void loadData() {
        String token = AccountUltil.BEARER + AccountUltil.getToken(requireContext());
        binding.progressBar.setVisibility(View.VISIBLE);

        BaseApi.API.getUserWithdrawals(token, status).enqueue(new Callback<WithdrawalListResponse>() {
            @Override
            public void onResponse(@NonNull Call<WithdrawalListResponse> call, @NonNull Response<WithdrawalListResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    WithdrawalListResponse listResponse = response.body();
                    if (listResponse.getCode() == 200) {
                        withdrawalList = listResponse.getData();
                        if (withdrawalList == null) {
                            withdrawalList = new ArrayList<>();
                        }
                        adapter.setWithdrawalList(withdrawalList);

                        if (withdrawalList.isEmpty()) {
                            binding.layoutDrum.setVisibility(View.VISIBLE);
                        } else {
                            binding.layoutDrum.setVisibility(View.GONE);
                        }
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorBody);
                            String errorMessage = errorJson.getString("message");
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG.toString, "Error parsing error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WithdrawalListResponse> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                Log.e(TAG.toString, "onFailure-getUserWithdrawals: " + t.getMessage());
            }
        });
    }

    @Override
    public void onWithdrawalClick(Withdrawal withdrawal) {
        if (requireActivity() instanceof WithdrawalActivity) {
            ((WithdrawalActivity) requireActivity()).showWithdrawalDetailDialog(withdrawal);
        }
    }
}
