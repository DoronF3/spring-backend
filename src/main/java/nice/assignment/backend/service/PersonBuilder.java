package nice.assignment.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import nice.assignment.backend.model.FinancialInfo;
import nice.assignment.backend.model.Person;
import nice.assignment.backend.model.PersonalInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PersonBuilder {

    public static Person buildPersonFromJson(JsonNode jsonInput) {
        try {
            if (isNull(jsonInput)) {
                throw new IllegalArgumentException("Invalid input format or missing required fields.");
            }

            validateJsonStructure(jsonInput);

            Long id = validateId(jsonInput);
            JsonNode personalInfoNode = jsonInput.get("personalInfo");
            String firstName = validateAndGetString(personalInfoNode, "firstName");
            String lastName = validateAndGetString(personalInfoNode, "lastName");
            String city = validateAndGetString(personalInfoNode, "city");
            JsonNode financialInfoNode = jsonInput.get("financialInfo");
            double cash = financialInfoNode.get("cash").asDouble();
            int numberOfAssets = validateNumberOfAssets(financialInfoNode);

            return Person.builder()
                    .id(id)
                    .personalInfo(PersonalInfo.builder()
                            .firstName(firstName)
                            .lastName(lastName)
                            .city(city)
                            .build())
                    .financialInfo(FinancialInfo.builder()
                            .cash(cash)
                            .numberOfAssets(numberOfAssets)
                            .build())
                    .build();

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid input format or missing required fields.", e);
        }
    }

    private static String validateAndGetString(JsonNode node, String fieldName) {
        String value = node.get(fieldName).asText();
        validateStringLength(value, fieldName);
        return value;
    }

    private static void validateStringLength(String value, String fieldName) {
        if (value == null || value.isEmpty() || value.length() > 30) {
            throw new IllegalArgumentException("Invalid value for field '" + fieldName + "'. Must be a non-empty string with length <= 30.");
        }
    }

    private static Long validateId(JsonNode jsonNode) {
        if (jsonNode.get("id").isLong()) {
            throw new IllegalArgumentException("Invalid id value. Must not be negative.");
        }
        long id = jsonNode.get("id").asLong();
        if (id < 0) {
            throw new IllegalArgumentException("Invalid id value. Must not be negative.");
        }
        return id;
    }

    private static int validateNumberOfAssets(JsonNode financialInfoNode) {
        int numberOfAssets = financialInfoNode.get("numberOfAssets").asInt();
        if (numberOfAssets < 0) {
            throw new IllegalArgumentException("Invalid numberOfAssets value. Must not be negative.");
        }
        return numberOfAssets;
    }

    private static void validateJsonStructure(JsonNode jsonNode) {
        Set<String> allowedFields = new HashSet<>(Arrays.asList("id", "personalInfo", "financialInfo"));
        validateFields(jsonNode, allowedFields, "JSON");

        validatePersonalInfo(jsonNode.get("personalInfo"));
        validateFinancialInfo(jsonNode.get("financialInfo"));
    }

    private static void validatePersonalInfo(JsonNode personalInfoNode) {
        Set<String> allowedFields = new HashSet<>(Arrays.asList("firstName", "lastName", "city"));
        validateFields(personalInfoNode, allowedFields, "personalInfo");
        validatePersonalInfoValues(personalInfoNode);
    }

    private static void validateFinancialInfo(JsonNode financialInfoNode) {
        if (financialInfoNode == null || !financialInfoNode.isObject()) {
            throw new IllegalArgumentException("Invalid financialInfo structure. Missing or not an object.");
        }

        Set<String> allowedFields = new HashSet<>(Arrays.asList("cash", "numberOfAssets"));
        validateFields(financialInfoNode, allowedFields, "financialInfo");
        validateFinancialInfoValues(financialInfoNode);
    }

    private static void validateFields(JsonNode node, Set<String> allowedFields, String sectionName) {
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (!allowedFields.contains(fieldName)) {
                throw new IllegalArgumentException("Invalid " + sectionName + " structure. Extra field found: " + fieldName);
            }
        }
    }

    private static void validatePersonalInfoValues(JsonNode personalInfoNode) {
        JsonNode firstNameNode = personalInfoNode.get("firstName");
        JsonNode lastNameNode = personalInfoNode.get("lastName");
        JsonNode cityNode = personalInfoNode.get("city");

        if (isNull(firstNameNode) || !firstNameNode.isTextual() ||
                isNull(lastNameNode) || !lastNameNode.isTextual() ||
                isNull(cityNode) || !cityNode.isTextual()) {
            throw new IllegalArgumentException("Invalid personalInfo structure. Incorrect or missing field types.");
        }
    }

    private static void validateFinancialInfoValues(JsonNode financialInfoNode) {
        JsonNode cashNode = financialInfoNode.get("cash");
        JsonNode numberOfAssetsNode = financialInfoNode.get("numberOfAssets");

        if (isNull(cashNode) || !cashNode.isNumber() ||
                isNull(numberOfAssetsNode) || !numberOfAssetsNode.isInt()) {
            throw new IllegalArgumentException("Invalid financialInfo structure. Incorrect or missing field types.");
        }
    }


    private static boolean isNull(JsonNode node) {
        return node == null || node.isNull();
    }

}
