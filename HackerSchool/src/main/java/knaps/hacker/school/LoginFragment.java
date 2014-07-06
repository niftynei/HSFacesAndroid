package knaps.hacker.school;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

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
    private View mErrorView;

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
        mErrorView = view.findViewById(R.id.error_screen);

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
            public void onPageFinished(final WebView view, final String url) {
                mProgressView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
                Log.e("LoginFragment", "Error loading the webpage. Error code " + errorCode);
                webpageLoadError(description);
            }

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
                        webpageLoadError(message);
                    }
                    else {
                        //something else happened. show an error screen
                        webpageLoadError(null);
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

    private void webpageLoadError(String message) {
        ((TextView) mErrorView.findViewById(R.id.error_message)).setText(message);
        mProgressView.setVisibility(View.GONE);
        mWebView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
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
        mErrorView.setVisibility(View.VISIBLE);
        ((TextView) mErrorView.findViewById(R.id.error_message)).setText(R.string.somethings_wrong);
    }
}
