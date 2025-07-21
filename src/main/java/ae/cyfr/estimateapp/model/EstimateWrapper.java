package ae.cyfr.estimateapp.model;

import java.util.ArrayList;
import java.util.List;

public class EstimateWrapper {

    private List<Estimate> estimates;

    public EstimateWrapper() {
        this.estimates = new ArrayList<>();
    }

    public List<Estimate> getEstimates() {
        return estimates;
    }

    public void setEstimates(List<Estimate> estimates) {
        this.estimates = estimates;
    }
}
