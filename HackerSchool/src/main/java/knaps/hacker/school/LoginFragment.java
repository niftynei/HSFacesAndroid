package knaps.hacker.school;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import knaps.hacker.school.networking.HSOAuthService;
import knaps.hacker.school.utils.Constants;

/**
 * This fragment displays a web view where you can log into (aka authenticate) the app.
 * <p/>
 * After login, this fragment disappears.
 */
public class LoginFragment extends Fragment implements HSOAuthService.RequestCallback {

    private WebView mWebView;
    private View mProgressView;

    // TODO: what if rotates!?!

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);
        mWebView = (WebView) view.findViewById(R.id.webView);
        mProgressView = view.findViewById(R.id.loading_view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupWebView();
    }

    private void setupWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                if (url.startsWith(Constants.REDIRECT_URI)) {
                    Uri uri = Uri.parse(url);
                    if (uri.getQueryParameter("code") != null) {
                        String code = uri.getQueryParameter("code");
                        fetchAccessToken(code);
                        return true;
                    }
                    else if (uri.getQueryParameter("error") != null) {
                        String message = uri.getQueryParameter("error_message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        mWebView.setVisibility(View.GONE);
                        // TODO: error messaging for URL stuffs
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
        mWebView.loadUrl(HSOAuthService.getService().getAuthUrl());
    }

    private void fetchAccessToken(String code) {
        mProgressView.setVisibility(View.VISIBLE);
        HSOAuthService.getService().getAccessToken(code, this);
    }

    @Override
    public void onSuccess() {
        mProgressView.setVisibility(View.GONE);

        Activity activity;
        if ((activity = getActivity()) != null) {
            FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
            transaction.remove(this);
            transaction.commit();
        }
    }

    @Override
    public void onFailure() {
        mProgressView.setVisibility(View.GONE);
        // TODO: Show a login failure message
    }
}
