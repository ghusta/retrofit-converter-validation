package io.github.ghusta.retrofit2.validation;

import javax.validation.groups.Default;

/**
 * Inspired by <a href="https://quarkus.io/guides/validation#validation-groups-for-rest-endpoint-or-service-method-validation">Quarkus doc</a>.
 */
public interface ValidationGroups {

    interface Creation extends Default {
    }

    interface Update extends Default {
    }

}
