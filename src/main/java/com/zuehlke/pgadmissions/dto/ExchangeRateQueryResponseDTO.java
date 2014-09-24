package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.zuehlke.pgadmissions.dto.converters.JsonDateTimeDeserializer;

public class ExchangeRateQueryResponseDTO {

    private Query query;
    
    public final Query getQuery() {
        return query;
    }

    public final void setQuery(Query query) {
        this.query = query;
    }

    public static class Query {
        
        private Integer count;
        
        @JsonDeserialize(using = JsonDateTimeDeserializer.class)
        private DateTime created;
        
        private String lang;
        
        private Results results;
        
        public final Results getResults() {
            return results;
        }

        public final void setResults(Results results) {
            this.results = results;
        }

        public final Integer getCount() {
            return count;
        }

        public final void setCount(Integer count) {
            this.count = count;
        }

        public final DateTime getCreated() {
            return created;
        }

        public final void setCreated(DateTime created) {
            this.created = created;
        }

        public final String getLang() {
            return lang;
        }

        public final void setLang(String lang) {
            this.lang = lang;
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
