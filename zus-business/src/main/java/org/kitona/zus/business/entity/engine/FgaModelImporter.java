package org.kitona.zus.business.entity.engine;

import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.business.entity.bo.TypeDefinition;
import org.kitona.zus.business.entity.graph.AuthorizationModelGraph;
import org.kitona.zus.business.entity.model.AuthorizationModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
public class FgaModelImporter {

    private FgaModelImporter() {
        throw new IllegalStateException("Utility class");
    }

    public static AuthorizationModelGraph importFromFga(InputStream inputStream) {

        // FGA DSL 格式
        try {
            String modelText = new String(inputStream.readAllBytes());
            List<TypeDefinition> typeDefList = FgaModelParser.parseModelText(modelText);
            return AuthorizationModelGraph.fromModel(new AuthorizationModel(typeDefList));
        } catch (IOException e) {
            throw new IllegalArgumentException("No 'model:' section found in FGA");
        }
    }


    public static AuthorizationModelGraph importFromFga(URI uri) {
        try (InputStream urlInputStream = uri.toURL().openStream()) {
            return FgaModelImporter.importFromFga(urlInputStream);
        } catch (IOException e) {
            log.error("FgaYamlModelImporter Failed to load model from URI: {}", uri, e);
            return null;
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        System.out.println(FgaModelImporter.importFromFga(new URI("https://raw.githubusercontent.com/openfga/sample-stores/refs/heads/main/stores/iot/model.fga")));
    }
}
