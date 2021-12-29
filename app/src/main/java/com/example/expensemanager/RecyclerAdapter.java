package com.example.expensemanager;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerAdapter extends RecyclerView.Adapter {
    List<Data> adapterList;
    Context mcontext;
    String fragement;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_recycler_data, parent, false);

        return new MyViewHolder(view, adapterList,mcontext,fragement);
    }

    public RecyclerAdapter(List<Data> list,Context mcontext,String fragement) {
        this.adapterList = list;
        this.mcontext=mcontext;
        this.fragement=fragement;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder)holder).setDetails(position);
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }


}

class MyViewHolder extends RecyclerView.ViewHolder
{

    View mView;
    List<Data> explist;
    Context context;
    String fragement;
    public MyViewHolder(@NonNull View itemView,List list,Context context,String fragement) {
        super(itemView);
        mView=itemView;
        explist=list;
        this.context=context;
        this.fragement=fragement;
    }

    public void setDetails(int position){
        TextView mDate=mView.findViewById(R.id.date_txt_expense);
        mDate.setText(explist.get(position).getDate());

        TextView mType=mView.findViewById(R.id.type_txt_expense);
        mType.setText(explist.get(position).getType());
        TextView mAAmount=mView.findViewById(R.id.ammount_txt_expense);

        String strammount=String.valueOf(explist.get(position).getAmount());
        mAAmount.setText(strammount);
        TextView mNote=mView.findViewById(R.id.note_txt_expense);
        mNote.setText(explist.get(position).getNote());


        mView.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View myview = inflater.inflate(R.layout.custom_layout, null);
                mydialog.setView(myview);
                final AlertDialog dialog = mydialog.create();

                dialog.setCancelable(false);
                final EditText edtAmmount = myview.findViewById(R.id.ammount_edt);
                final EditText edtType = myview.findViewById(R.id.type_edt);
                final EditText edtNote = myview.findViewById(R.id.note_edt);

                Button btnSave = myview.findViewById(R.id.btnSave);
                Button btnCansel = myview.findViewById(R.id.btnCancel);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type = edtType.getText().toString().trim();
                        String ammount = edtAmmount.getText().toString().trim();
                        String note = edtNote.getText().toString().trim();

                        if (TextUtils.isEmpty(type)) {
                            edtType.setError("Required Field..");
                            return;
                        }

                        if (TextUtils.isEmpty(ammount)) {
                            edtAmmount.setError("Required Field..");
                            return;
                        }

                        int ourammontint = Integer.parseInt(ammount);

                        if (TextUtils.isEmpty(note)) {
                            edtNote.setError("Required Field..");
                            return;
                        }
                        FirebaseAuth mUser = FirebaseAuth.getInstance();
                        String uid = mUser.getUid();
                        DatabaseReference mIncomeDatabase;

                        if (fragement.equals("Income"))
                            mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid).child(explist.get(position).getId());
                        else
                            mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid).child(explist.get(position).getId());

                        String id = mIncomeDatabase.push().getKey();
                        String mDate = DateFormat.getDateInstance().format(new Date());

                        Data data = new Data(ourammontint, type, note, id, mDate);
                        mIncomeDatabase.setValue(data);

                        explist.get(position).setAmount(Integer.parseInt(ammount));
                        explist.get(position).setNote(note);
                        explist.get(position).setType(type);
                        if (fragement.equals("Income")) {
                            IncomeFragment.adapter.notifyDataSetChanged();

                        } else
                            ExpenseFragment.adapter.notifyDataSetChanged();
                        Toast.makeText(context, "Data ADDED", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });
                btnCansel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mUser = FirebaseAuth.getInstance();
                String uid = mUser.getUid();
                DatabaseReference mIncomeDatabase;
                if (fragement.equals("Income"))
                    mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid).child(explist.get(position).getId());
                else
                    mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid).child(explist.get(position).getId());
                Map<String,Object> deleteValue=new HashMap<>();
                mIncomeDatabase.setValue(deleteValue);

                explist.remove(position);
                if (fragement.equals("Income")) {
                    IncomeFragment.adapter.notifyDataSetChanged();

                } else
                    ExpenseFragment.adapter.notifyDataSetChanged();
                Toast.makeText(context, "Data DELETED", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
