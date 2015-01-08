package mango.mechanics.controls;

import java.text.ParseException;
import javax.swing.text.DefaultFormatter;

public class PositiveRealNumberFormat extends DefaultFormatter {
    public PositiveRealNumberFormat() {
        super();
    }
    
    @Override
    public String valueToString(Object O) throws ParseException {
        return super.valueToString(O);
    }
    @Override
    public Object stringToValue(String S) throws ParseException {
        double r = Double.parseDouble(S);
        String rString = Double.toString(r);
        
        rString = rString.replaceAll("/D[^/.]", "");
        return Double.parseDouble(rString);
    }
}
