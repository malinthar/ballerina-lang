package org.ballerinalang.langserver.contexts;

import com.google.gson.Gson;
import org.ballerinalang.formatter.core.ForceFormattingOptions;
import org.ballerinalang.formatter.core.FormattingOptions;
import org.ballerinalang.langserver.LSContextOperation;
import org.ballerinalang.langserver.commons.FormattingContext;
import org.ballerinalang.langserver.commons.LSOperation;
import org.ballerinalang.langserver.commons.LanguageServerContext;
import org.ballerinalang.langserver.commons.capability.LSClientCapabilities;
import org.ballerinalang.langserver.commons.workspace.WorkspaceManager;
import org.eclipse.lsp4j.DocumentFormattingParams;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Formatting context implementation.
 *
 * @since 2201.7.0
 */
public class FormattingContextImpl extends AbstractDocumentServiceContext implements FormattingContext {

    LSClientCapabilities clientCapabilities;

    DocumentFormattingParams params;

    FormattingOptions formattingOptions;

    FormattingContextImpl(LSOperation operation, String fileUri, WorkspaceManager wsManager,
                          LanguageServerContext serverContext,
                          LSClientCapabilities clientCapabilities,
                          DocumentFormattingParams params) {
        super(operation, fileUri, wsManager, serverContext);
        this.clientCapabilities = clientCapabilities;
        this.params = params;
    }

    @Override
    public DocumentFormattingParams getParams() {
        return this.params;
    }

    @Override
    public LSClientCapabilities clientCapabilities() {
        return this.clientCapabilities;
    }

    @Override
    public FormattingOptions formattingOptions() {
        if (formattingOptions == null) {
            Path path = workspace().projectRoot(filePath());
            FormattingOptions.FormattingOptionsBuilder builder = FormattingOptions.builder();
            Gson gson = new Gson();
            try {
                BufferedReader bufferedReader =
                        new BufferedReader(new FileReader(path.resolve("styles.json").toString(),
                                StandardCharsets.UTF_8));
                ExtendedFormattingOptions extendedFormattingOptions =
                        gson.fromJson(bufferedReader, ExtendedFormattingOptions.class);
                if (extendedFormattingOptions.columnLimit != null) {
                    builder.setColumnLimit(extendedFormattingOptions.columnLimit);
                }
                if (extendedFormattingOptions.wsCharacter != null) {
                    builder.setWSCharacter(extendedFormattingOptions.wsCharacter);
                }
                if (extendedFormattingOptions.lineWrapping != null) {
                    builder.setLineWrapping(extendedFormattingOptions.lineWrapping);
                }
                if (extendedFormattingOptions.forceFormattingOptions != null) {
                    builder.setForceFormattingOptions(extendedFormattingOptions.forceFormattingOptions);
                }
                if (extendedFormattingOptions.tabSize != null) {
                    builder.setTabSize(extendedFormattingOptions.tabSize);
                    formattingOptions = builder.build();
                    return formattingOptions;
                }
            } catch (IOException e) {
                //ignore
            }
            formattingOptions = builder.setTabSize(params.getOptions().getTabSize()).build();
        }
        return formattingOptions;
    }

    /**
     * Represents Language server formatting context builder.
     *
     * @since 2201.7.0
     */
    protected static class FormattingContextBuilder extends
            AbstractContextBuilder<FormattingContextImpl.FormattingContextBuilder> {

        private DocumentFormattingParams params;
        private LSClientCapabilities clientCapabilities;

        public FormattingContextBuilder(DocumentFormattingParams params,
                                        LanguageServerContext serverContext,
                                        LSClientCapabilities clientCapabilities) {
            super(LSContextOperation.TXT_DOC_SYMBOL, serverContext);
            this.clientCapabilities = clientCapabilities;
            this.params = params;
        }

        public FormattingContextImpl build() {
            return new FormattingContextImpl(this.operation,
                    this.fileUri,
                    this.wsManager,
                    this.serverContext,
                    this.clientCapabilities,
                    this.params);
        }

        @Override
        public FormattingContextImpl.FormattingContextBuilder self() {
            return this;
        }
    }

    private static class ExtendedFormattingOptions {
        Integer tabSize;
        String wsCharacter;
        Integer columnLimit;
        Boolean lineWrapping;

        ForceFormattingOptions forceFormattingOptions;

    }
}
