package com.zuehlke.pgadmissions.dto.json;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExchangeRateLookupResponseDTO {

    private Query query;
    
    public final Query getQuery() {
        return query;
    }

    public final void setQuery(Query query) {
        this.query = query;
    }

    public static class Query {
        
        private Results results;
        
        public final Results getResults() {
            return results;
        }

        public final void setResults(Results results) {
            this.results = results;
        }

        public static class Results {
            
            private Rate rate;
            
            public final Rate getRate() {
                return rate;
            }

            public final void setRate(Rate rate) {
                this.rate = rate;
            }

            public static class Rate {
                
                @JsonProperty("Rate")
                private BigDecimal rate;
                
                public final BigDecimal getRate() {
                    return rate;
                }

                public final void setRate(BigDecimal rate) {
                    this.rate = rate;
                }
                
            }
            
        }
        
    }
    
}
