package org.kitona.zus.business.entity.bo;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Slf4j
public class FgaYamlModelImporter {

    private static final String MODEL_NODE_NAME = "model";

    private FgaYamlModelImporter() {
        throw new IllegalStateException("Utility class");
    }

    public static AuthorizationModelGraph importFromFgaYaml(InputStream inputStream){
        Map<String, Object> yamlRoot = new Yaml().load(inputStream);
        String modelText = (String) yamlRoot.get(MODEL_NODE_NAME);
        if (modelText == null) {
            throw new IllegalArgumentException("No 'model:' section found in YAML");
        }

        List<TypeDefinition> typeDefList = FgaYamlParser.parseModelText(modelText);
        return AuthorizationModelGraph.fromModel(new AuthorizationModel(typeDefList));
    }


    public static AuthorizationModelGraph importFromFgaYaml(URI uri) {
        try (InputStream urlInputStream = uri.toURL().openStream()) {
            return FgaYamlModelImporter.importFromFgaYaml(urlInputStream);
        } catch (IOException e) {
            log.error("FgaYamlModelImporter Failed to load model from URI: {}", uri, e);
            return null;
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        System.out.println(FgaYamlModelImporter.importFromFgaYaml(new URI("https://raw.githubusercontent.com/openfga/sample-stores/refs/heads/main/stores/multitenant-rbac/store.fga.yaml")));
    }
}
