package com.purebred.core.view.entity.field;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LabelDepot {
    private Map<String, String> entityTypeLabels = new TreeMap<String, String>();
    private Map<String, Set<String>> entityTypePropertyIds = new HashMap<String, Set<String>>();
    private Map<String, Set<DisplayLabel>> labels = new HashMap<String, Set<DisplayLabel>>();

    public void putEntityLabel(String entityType, String label) {
        entityTypeLabels.put(entityType, label);
    }

    public String getEntityLabel(String entityType) {
        return entityTypeLabels.get(entityType);
    }

    public Map<String, String> getEntityTypeLabels() {
        return entityTypeLabels;
    }

    public Map<Object, String> getPropertyIds(String entityType) {
        Map<Object, String> fieldItems = new LinkedHashMap<Object, String>();

        Set<String> propertyIds = entityTypePropertyIds.get(entityType);
        for (String propertyId : propertyIds) {
            fieldItems.put(propertyId, propertyId);
        }

        return fieldItems;
    }

    public void putFieldLabel(String entityType, String propertyId, String section, String label) {
        if (!entityTypePropertyIds.containsKey(entityType)) {
            entityTypePropertyIds.put(entityType, new TreeSet<String>());
        }

        Set<String> propertyIds = entityTypePropertyIds.get(entityType);
        if (!propertyIds.contains(propertyId)) {
            propertyIds.add(propertyId);
        }

        String propertyPath = entityType + "." + propertyId;
        if (!labels.containsKey(propertyPath)) {
            labels.put(propertyPath, new HashSet<DisplayLabel>());
        }

        Set<DisplayLabel> displayLabels = labels.get(propertyPath);

        DisplayLabel displayLabel = new DisplayLabel(propertyId, section, label);
        if (!displayLabels.contains(displayLabel)) {
            displayLabels.add(displayLabel);
        }
    }

    public String getFieldLabel(String entityType, String propertyId) {
        String propertyPath = entityType + "." + propertyId;
        String label = "";
        Set<DisplayLabel> displayLabels = labels.get(propertyPath);
        for (DisplayLabel displayLabel : displayLabels) {
            if (!label.isEmpty()) {
                label += ", ";
            }
            label += displayLabel.getDisplayName();
        }

        return label;
    }

    public void trackLabels(DisplayFields displayFields) {
        Collection<DisplayField> fields = displayFields.getFields();

        for (DisplayField field : fields) {
            String label = displayFields.getLabel(field.getPropertyId());
            if (label == null) {
                label = field.getPropertyId();
            } else {
                label = label.replaceAll("<.*>.*</.*>", "");
            }
            putFieldLabel(displayFields.getEntityType().getName(), field.getPropertyId(),
                    field.getLabelSectionDisplayName(), label);

        }
    }

    public static class DisplayLabel {
        private String propertyId;
        private String section;
        String label;

        public DisplayLabel(String propertyId, String section, String label) {
            this.propertyId = propertyId;
            this.section = section;
            this.label = label;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public String getDisplayName() {
            if (section == null || section.isEmpty()) {
                return label;
            } else {
                return label + " (" + section + ")";
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DisplayLabel that = (DisplayLabel) o;

            if (!label.equals(that.label)) return false;
            if (!propertyId.equals(that.propertyId)) return false;
            if (!section.equals(that.section)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = propertyId.hashCode();
            result = 31 * result + section.hashCode();
            result = 31 * result + label.hashCode();
            return result;
        }
    }
}
