package com.uttampanchasara.pdf

import android.os.Bundle
import android.print.CreatePdf
import android.print.PdfCallbackListener
import android.print.PrintAttributes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity(), PdfCallbackListener {

    override fun onSuccess(filePath: String) {
        Log.i("MainActivity", "Pdf Saved at: $filePath")

        Toast.makeText(this, "Pdf Saved at: $filePath", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
    }

    var openPrintDialog: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPrint = findViewById<Button>(R.id.btnPrint)
        val btnPrintAndSave = findViewById<Button>(R.id.btnPrintAndSave)
        btnPrint.setOnClickListener {
            openPrintDialog = false
            doPrint()
        }

        btnPrintAndSave.setOnClickListener {
            openPrintDialog = true
            doPrint()
        }
    }

    private fun doPrint() {
        CreatePdf(this)
            .setPdfName("Sample")
            .openPrintDialog(openPrintDialog)
            .setContentBaseUrl(null)
            .setPageSize(PrintAttributes.MediaSize.ISO_A4)
            .setFilePath(getExternalFilesDir(null)!!.absolutePath + "/MyPdf")
            .setContent(getString(R.string.content))
            .setCallbackListener(object : PdfCallbackListener {
                override fun onFailure(errorMsg: String) {
                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(filePath: String) {
                    Toast.makeText(this@MainActivity, "Pdf Saved at: $filePath", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            .create()
    }
}