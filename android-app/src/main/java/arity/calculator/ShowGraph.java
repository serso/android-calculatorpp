// Copyright (C) 2009 Mihai Preda

package arity.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import org.javia.arity.Function;

import java.util.ArrayList;

public class ShowGraph extends Activity {

    private GraphView view;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ArrayList<Function> funcs = Calculator.graphedFunction;
        if (funcs == null) {
            finish();
            return;
        }
        int size = funcs.size();
        if (size == 1) {
            Function f = funcs.get(0);
            view = f.arity() == 1 ? new Graph2dView(this) : new Graph3dView(this);
            view.setFunction(f);
        } else {
            view = new Graph2dView(this);
            ((Graph2dView) view).setFunctions(funcs);
        }
        setContentView((View) view);
    }

    protected void onPause() {
        super.onPause();
        view.onPause();
    }

    protected void onResume() {
        super.onResume();
        view.onResume();
    }

/*    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        (new MenuInflater(this)).inflate(R.menu.graph, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
        case R.id.capture_screenshot:
            String fileName = view.captureScreenshot();
            if (fileName != null) {
                Toast.makeText(this, "screenshot saved as \n" + fileName, Toast.LENGTH_LONG).show();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(new File(fileName)), "image/png");
                startActivity(i);
            }
            break;

        default:
            return false;
        }
        return true;
    }*/
}
