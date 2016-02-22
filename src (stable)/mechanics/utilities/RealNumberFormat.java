package mechanics.utilities;

import java.text.ParseException;
import javax.swing.text.DefaultFormatter;

public class RealNumberFormat extends DefaultFormatter {
    public RealNumberFormat() {
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
        
        if("-".equals(rString.substring(0, 1))){
            rString = rString.replaceAll("/D[^/.]", "");
            rString = "-"+rString;
        } else {
            rString = rString.replaceAll("/D[^/./-]", "");
        }
        return Double.parseDouble(rString);
    }
}
