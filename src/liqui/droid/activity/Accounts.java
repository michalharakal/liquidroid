package liqui.droid.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import liqui.droid.Constants;
import liqui.droid.holder.BreadCrumbHolder;
import liqui.droid.R;

public class Accounts extends Base implements OnItemClickListener {
    
    protected AccountManager accountManager;
    protected Intent intent;
    protected ListView listView;
    
    protected AccountsAdapter mAdapter;
    
    protected Button mButtonAccountAdd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_account_list);
        setUpActionBar();
        setBreadCrumbs();
        
        accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("liqui.droid.account");
        
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new AccountsAdapter(this, R.layout.row_account, accounts));
        listView.setOnItemClickListener(this);
        
        mButtonAccountAdd = (Button) findViewById(R.id.btn_account_add);
        mButtonAccountAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                accountAdd();
            }
        });

    }
    
    protected void accountAdd() {
        Intent intent = new Intent().setClass(Accounts.this, LiquiDroid.class);
        
        intent.setAction("liqui.droid.sync.LOGIN");
        startActivity(intent);
        
        finish();
    }
    
    /**
     * Sets the bread crumbs.
     */
    protected void setBreadCrumbs() {
        BreadCrumbHolder[] breadCrumbHolders = new BreadCrumbHolder[1];

        BreadCrumbHolder b = new BreadCrumbHolder();
        b.setLabel(getResources().getString(R.string.title_explore));
        b.setTag(Constants.EXPLORE);
        breadCrumbHolders[0] = b;
            
        createBreadcrumb(getString(R.string.menu_accounts), breadCrumbHolders);
    }

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Account account = (Account)listView.getItemAtPosition(position);
        Intent intent = new Intent();
        intent.putExtra("account", account);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    
    public class AccountsAdapter extends ArrayAdapter<Account> {

        public AccountsAdapter(Context context, int textViewResourceId, Account[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;
            
            if(row == null)
            {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.row_account, parent, false);
                
                holder = new ViewHolder();
                holder.tvTitle = (TextView)row.findViewById(R.id.tv_title);
                holder.tvDesc  = (TextView)row.findViewById(R.id.tv_desc);
                
                row.setTag(holder);
            } else {
                holder = (ViewHolder)row.getTag();
            }
            
            Account account = getItem(position);
            holder.tvTitle.setText(account.name);
            holder.tvDesc.setText(accountManager.getUserData(account, Constants.Account.API_URL));
            
            return row;
        }
        
        class ViewHolder {
            TextView tvTitle;
            TextView tvDesc;
        }
    }
}
