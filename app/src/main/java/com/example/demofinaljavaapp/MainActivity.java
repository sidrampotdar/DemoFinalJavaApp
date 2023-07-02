package com.example.demofinaljavaapp;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class MainActivity extends AppCompatActivity {

    private Button createFileButton, deleteFileButton, compileRunButton;
    private EditText sourceCodeEditText;
    private TextView outputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFileButton = findViewById(R.id.createFileButton);
        deleteFileButton = findViewById(R.id.deleteFileButton);
        compileRunButton = findViewById(R.id.compileRunButton);
        sourceCodeEditText = findViewById(R.id.sourceCodeEditText);
        outputTextView = findViewById(R.id.outputTextView);

        createFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFile();
            }
        });

        deleteFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile();
            }
        });

        compileRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compileAndRun();
            }
        });
    }

    private void createFile() {
        try {
            String fileName = "MyFile.java";
            File file = new File(getFilesDir(), fileName);
            FileWriter writer = new FileWriter(file);
            writer.write(sourceCodeEditText.getText().toString());
            writer.close();
            outputTextView.setText("File created: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            outputTextView.setText("Error creating file");
        }
    }

    private void deleteFile() {
        String fileName = "MyFile.java";
        File file = new File(getFilesDir(), fileName);
        if (file.exists() && file.delete()) {
            outputTextView.setText("File deleted: " + fileName);
        } else {
            outputTextView.setText("Error deleting file");
        }
    }

    private void compileAndRun() {
        String fileName = "MyFile.java";
        File file = new File(getFilesDir(), fileName);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, null, null);

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(
                List.of(file));

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticCollector, null, null, compilationUnits);

        boolean success = task.call();

        if (success) {
            outputTextView.setText("Compilation successful");

            // Run the compiled code
            try {
                String className = "MyFile";
                Class<?> myClass = Class.forName(className);
                Runnable runnable = (Runnable) myClass.getDeclaredConstructor().newInstance();
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
                outputTextView.setText("Error running code");
            }
        } else {
            StringBuilder errorMessage = new StringBuilder("Compilation error:\n");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticCollector.getDiagnostics()) {
                errorMessage.append(diagnostic.getMessage(null)).append("\n");
            }
            outputTextView.setText(errorMessage.toString());
        }
    }
}
