package com.zuehlke.pgadmissions.dto.json;

import java.util.List;

public class InstitutionSearchResponseDTO {
    
    private List<Item> items;

    public final List<Item> getItems() {
        return items;
    }

    public final void setItems(List<Item> items) {
        this.items = items;
    }
    
    public static class Item {

        private String title;
        
        private String link;

        public final String getTitle() {
            return title;
        }

        public final void setTitle(String title) {
            this.title = title;
        }

        public final String getLink() {
            return link;
        }

        public final void setLink(String link) {
            this.link = link;
        } 
        
    }

}
