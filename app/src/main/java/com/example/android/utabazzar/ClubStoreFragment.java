package com.example.android.utabazzar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClubStoreFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClubStoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClubStoreFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SharedPreferences sharedPreferences;
    String myPreferences = "MY_PREFERENCES";
    String emailId = "email_id";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ListView listView;

    Context context;
    private RecyclerView recyclerView;
    private club_adapter adapter;
    private List<Album> albumList;
    public static ArrayList<String> images;
    ArrayList<String> product_name;
    ArrayList<String> product_price;
    ArrayList<String> product_key;
    ArrayList<String> seller_name;
    ArrayList<String> seller_phone;
    ArrayList<String> seller_email;

    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseAuth mAuth;

    FirebaseUser user;

    String thisClubName;
    public static String url;

    public ClubStoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClubStoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClubStoreFragment newInstance(String param1, String param2) {
        ClubStoreFragment fragment = new ClubStoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        sharedPreferences = getActivity().getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        if (bundle != null) {
            thisClubName = bundle.getString("CLUB_NAME", "Club name");
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("club_management").child("clubs").child(thisClubName).child("products");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("/images");

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        final List<String> product_list = new ArrayList<String>();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_list_item_1, product_list);

        // Inflate the layout for this fragment
        context = getContext();

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup container = (ViewGroup)getView();

        View view = inflater.inflate(R.layout.fragment_club_store, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        albumList = new ArrayList<>();
        images = new ArrayList<>();
        product_name = new ArrayList<>();
        product_price = new ArrayList<>();
        product_key = new ArrayList<>();
        seller_name = new ArrayList<>();
        seller_phone = new ArrayList<>();
        seller_email = new ArrayList<>();
        adapter = new club_adapter(getContext(), albumList);

//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //Read from database
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String productKey = ds.getKey();
                    String productName = (String) ds.child("productName").getValue();
                    String price = (String) ds.child("productPrice").getValue();
                    String sellerName = (String) ds.child("sellerName").getValue();
                    String phone = (String) ds.child("sellerPhone").getValue();
                    String email = (String) ds.child("sellerEmail").getValue();
                    storageReference.child(productKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri downloadUrl)
                        {
                             url = downloadUrl.toString();

                             //System.out.println(url);
                        }
                    });
                    product_name.add(productName);
                    product_price.add(price);
                    product_key.add(productKey);
                    seller_name.add(sellerName);
                    seller_phone.add(phone);
                    seller_email.add(email);
                    images.add(url);
                    product_list.add(productKey);
                }
                arrayAdapter.notifyDataSetChanged();
                prepareAlbums();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        myRef.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();

        View view = inflater.inflate(R.layout.fragment_club_store, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "http://52.90.174.26:8000" + "/user/getProducts/",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //prepareAlbums();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getContext(), NoInternetActivity.class);
//                        startActivity(intent);
                    }
                }
        );
        SingletonRequestQueue.getInstance(context).addToRequestQueue(jsonArrayRequest);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragment();
            }
        });
        return view;
    }

    public static String returnUrl(int position){
        System.out.print("in returnUrl: " + images.get(position));
        return images.get(position);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Adding few albums for testing
     */
    private void prepareAlbums() {
        for (int i = 0; i < images.size(); i++) {
            Album a = new Album(product_name.get(i), product_price.get(i), images.get(i), product_key.get(i), seller_name.get(i), seller_phone.get(i), seller_email.get(i), "","","");
            albumList.add(a);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void refreshFragment() {


        albumList = new ArrayList<>();
        images = new ArrayList<>();
        product_name = new ArrayList<>();
        product_price = new ArrayList<>();
        product_key = new ArrayList<>();
        seller_name = new ArrayList<>();
        seller_phone = new ArrayList<>();
        seller_email = new ArrayList<>();
        adapter = new club_adapter(getContext(), albumList);

//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "http://52.90.174.26:8000" + "/user/getProducts/",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Loop through the array elements
                            for (int i = 0; i < response.length(); i++) {
                                // Get current json object
                                JSONObject product = response.getJSONObject(i);
                                String url = product.getString("image");
                                String name = product.getString("product_name");
                                String price = product.getString("product_price");
                                String id = product.getString("id");
                                String s_name = product.getString("seller_name");
                                String phone = product.getString("seller_phone");
                                String email = product.getString("seller_email");
                                String block = product.getString("seller_block");
                                String room = product.getString("seller_room");
                                String time = product.getString("time_period");
                                //images.add(url);
                                product_name.add(name);
                                product_price.add(price);
                                product_key.add(id);
                                seller_name.add(s_name);
                                seller_phone.add(phone);
                                seller_email.add(email);
                            }
                            prepareAlbums();
//                            Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);
//                        Intent intent = new Intent(getContext(), NoInternetActivity.class);
//                        startActivity(intent);
                    }
                }
        );
        SingletonRequestQueue.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener( new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }


}
