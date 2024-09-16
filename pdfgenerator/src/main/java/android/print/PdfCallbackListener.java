package android.print;

public interface PdfCallbackListener {
    void onFailure(String errorMsg);
    void onSuccess(String filePath);
}
