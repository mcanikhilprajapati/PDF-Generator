package android.print;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class CreatePdf {
    private final Context mContext;
    private String MimeType = "text/html";
    private String ENCODING = "utf-8";
    private String mBaseURL = "";
    private String mPdfName = "";
    private PdfCallbackListener mCallbacks = null;
    private String mContent = "";
    private boolean doPrint = false;
    private String mPdfFilePath = "";
    private WebView mWebView = null;
    private PrintAttributes.MediaSize mMediaSize = null;

    public CreatePdf(Context mContext) {
        this.mContext = mContext;
    }

    public CreatePdf setPdfName( String pdfName) {
        this.mPdfName = pdfName + ".pdf";
        return this;
    }

    public CreatePdf setContentBaseUrl( String baseUrl) {
        this.mBaseURL = baseUrl;
        return this;
    }

    public CreatePdf setContent( String content) {
        this.mContent = content;
        return this;
    }

    public CreatePdf openPrintDialog(boolean doPrint) {
        this.doPrint = doPrint;
        return this;
    }

    public CreatePdf setFilePath(String pdfFilePath) {
        this.mPdfFilePath = pdfFilePath;
        return this;
    }

    public CreatePdf setPageSize( PrintAttributes.MediaSize size) {
        this.mMediaSize = size;
        return this;
    }

    public CreatePdf setCallbackListener( PdfCallbackListener callbacks) {
        this.mCallbacks = callbacks;
        return this;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void create() {
        if (mPdfName.isEmpty()) {
            mCallbacks.onFailure("Pdf name must not be empty.");
            return;
        }

        if (mMediaSize == null) {
            mCallbacks.onFailure("Page Size must not be empty.");
            return;
        }

        if (mContent.isEmpty()) {
            mCallbacks.onFailure("Empty or null content.");
            return;
        }

        mWebView = new WebView(mContext);
        mWebView.loadDataWithBaseURL(mBaseURL, mContent, MimeType, ENCODING, null);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.clearCache(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(mPdfName);
                savePdf(printAdapter);
            }
        });
    }

    private void savePdf(PrintDocumentAdapter printAdapter) {
        String filePath = "";
        if (mPdfFilePath.isEmpty()) {
            filePath = mContext.getCacheDir().getAbsolutePath();
        } else {
            filePath = mPdfFilePath;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(mMediaSize)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        new PdfPrint(printAttributes).print(
                printAdapter,
                file,
                mPdfName,
                new PdfPrint.CallbackPrint() {
                    @Override
                    public void success(String path) {
                        mCallbacks.onSuccess(path);

                        if (doPrint) {
                            PrintManager printManager = (PrintManager) mContext.getSystemService(Context.PRINT_SERVICE);
                            printManager.print(
                                    mPdfName,
                                    printAdapter,
                                    new PrintAttributes.Builder().build()
                            );
                        }
                        mWebView = null;
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        mCallbacks.onFailure(errorMsg);
                        mWebView = null;
                    }
                });
    }
}