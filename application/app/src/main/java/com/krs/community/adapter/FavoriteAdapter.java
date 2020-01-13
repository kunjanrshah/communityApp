package com.krs.community.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.iammert.library.ui.multisearchviewlib.databinding.ViewItemBinding;
import com.krs.community.R;
import com.krs.community.databinding.ContentFavoriteItemBinding;
import com.krs.community.model.Member;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by rudsonlima on 19/03/18.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private ArrayList<Member> mMembers;
    private ArrayList<Member> mSearchMembers;

    public FavoriteAdapter(ArrayList<Member> Members) {
        this.mMembers = Members;
        this.mSearchMembers = new ArrayList<>();
        this.mSearchMembers.addAll(this.mMembers);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        ContentFavoriteItemBinding getBinding() {
            return DataBindingUtil.getBinding(itemView);
        }
    }

    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.content_favorite_item, parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = mMembers.get(holder.getAdapterPosition());

        holder.getBinding().setVariable(com.krs.community.BR.member, member);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return (mMembers == null ? 0 : mMembers.size());
    }

    private static String removeAccent(String text) {
        String result = Normalizer.normalize(text, Normalizer.Form.NFD);
        return result.replaceAll("[^\\p{ASCII}]", "");
    }

    void searchMembers(CharSequence charText) {

        charText = removeAccent((String) charText).toLowerCase(Locale.getDefault());

        mMembers.clear();
        if (charText.length() == 0) {
            mMembers.addAll(mSearchMembers);
        } else {
            for (Member Member : mSearchMembers) {
                String name = removeAccent(Member.getFirstName());
                if (name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mMembers.add(Member);
                }
            }
        }

        notifyDataSetChanged();
    }
}

