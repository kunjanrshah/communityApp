package com.krs.community.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.krs.community.R;
import com.krs.community.fragments.SearchCityResult;

public class AtoZBottomAdapter extends BaseAdapter {

    private Context _context;
    private ISortingRecords mISortingRecords;

    public AtoZBottomAdapter(Context _context) {
        this._context = _context;
    }

    public void setmISortingRecords(ISortingRecords mISortingRecords) {
        this.mISortingRecords = mISortingRecords;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AtoZViewHolder viewHolder;
        LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.bottom_sheet_atoz_dialog, null);
            viewHolder = new AtoZViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AtoZViewHolder) convertView.getTag();
        }
        viewHolder.tv_a.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.Selected, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.a));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_b.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedB, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.b));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_c.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedC, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.c));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_d.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelecetdD, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.d));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_e.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelecetdE, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.e));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_f.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelecetdF, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.f));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_g.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedG, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.g));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_h.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelecetdH, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.h));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_i.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelecetdeI, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.i));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_j.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelecetedJ, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.j));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_k.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelecetedK, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.k));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_l.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedL, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.l));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_m.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedM, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.m));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_n.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedN, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.n));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_o.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedO, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.o));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_p.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedP, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.p));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_q.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedQ, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.q));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_r.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedR, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.r));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_s.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedS, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.s));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_t.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedT, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.t));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }

        });
        viewHolder.tv_u.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedU, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.u));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_v.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedV, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.v));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_w.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedW, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.w));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_x.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedX, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.x));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_y.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedY, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.y));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_z.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedZ, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha(_context.getString(R.string.z));
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });

        viewHolder.tv_all.setOnClickListener(v -> {
            Toast.makeText(_context, R.string.SelectedAll, Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });

        return convertView;
    }

    public interface ISortingRecords {
        void getRecords();
    }

    private class AtoZViewHolder {
        TextView tv_a, tv_b, tv_c, tv_d, tv_e, tv_f, tv_g, tv_h, tv_i, tv_j, tv_k, tv_l,
                tv_m, tv_n, tv_o, tv_p, tv_q, tv_r, tv_s, tv_t, tv_u, tv_v, tv_w, tv_x, tv_y, tv_z, tv_all;

        AtoZViewHolder(View view) {
            tv_a = view.findViewById(R.id.tv_a);
            tv_b = view.findViewById(R.id.tv_b);
            tv_c = view.findViewById(R.id.tv_c);
            tv_d = view.findViewById(R.id.tv_d);
            tv_e = view.findViewById(R.id.tv_e);
            tv_f = view.findViewById(R.id.tv_f);
            tv_g = view.findViewById(R.id.tv_g);
            tv_h = view.findViewById(R.id.tv_h);
            tv_i = view.findViewById(R.id.tv_i);
            tv_j = view.findViewById(R.id.tv_j);
            tv_k = view.findViewById(R.id.tv_k);
            tv_l = view.findViewById(R.id.tv_l);
            tv_m = view.findViewById(R.id.tv_m);
            tv_n = view.findViewById(R.id.tv_n);
            tv_o = view.findViewById(R.id.tv_o);
            tv_p = view.findViewById(R.id.tv_p);
            tv_q = view.findViewById(R.id.tv_q);
            tv_r = view.findViewById(R.id.tv_r);
            tv_s = view.findViewById(R.id.tv_s);
            tv_t = view.findViewById(R.id.tv_t);
            tv_u = view.findViewById(R.id.tv_u);
            tv_v = view.findViewById(R.id.tv_v);
            tv_w = view.findViewById(R.id.tv_w);
            tv_x = view.findViewById(R.id.tv_x);
            tv_y = view.findViewById(R.id.tv_y);
            tv_z = view.findViewById(R.id.tv_z);
            tv_all = view.findViewById(R.id.tv_all);
        }
    }

}
