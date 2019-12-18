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
            Toast.makeText(_context, "Selected 'A'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("A");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_b.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'B'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("B");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_c.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'C'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("C");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_d.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'D'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("D");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_e.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'E'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("E");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_f.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'F'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("F");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_g.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'G'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("G");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_h.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'H'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("H");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_i.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'I'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("I");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_j.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'J'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("J");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_k.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'K'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("K");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_l.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'L'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("L");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_m.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'M'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("M");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_n.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'N'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("N");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_o.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'O'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("O");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_p.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'P'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("p");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_q.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'Q'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("Q");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_r.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'R'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("R");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_s.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'S'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("S");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_t.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'T'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("T");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }

        });
        viewHolder.tv_u.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'U'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("U");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_v.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'V'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("V");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_w.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'W'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("W");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_x.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'X'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("X");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_y.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'Y'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("Y");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });
        viewHolder.tv_z.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'Z'", Toast.LENGTH_SHORT).show();
            SearchCityResult.Companion.setAlpha("Z");
            mISortingRecords.getRecords();
            if (SearchCityResult.Companion.getDialog() != null) {
                SearchCityResult.Companion.getDialog().dismiss();
            }
        });

        viewHolder.tv_all.setOnClickListener(v -> {
            Toast.makeText(_context, "Selected 'ALL'", Toast.LENGTH_SHORT).show();
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
                tv_m, tv_n, tv_o, tv_p, tv_q, tv_r, tv_s, tv_t, tv_u, tv_v, tv_w, tv_x, tv_y, tv_z,tv_all;

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
            tv_all= view.findViewById(R.id.tv_all);
        }
    }

}
