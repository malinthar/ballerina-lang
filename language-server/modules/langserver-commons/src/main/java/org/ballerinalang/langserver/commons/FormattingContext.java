package org.ballerinalang.langserver.commons;

import org.ballerinalang.formatter.core.FormattingOptions;
import org.ballerinalang.langserver.commons.capability.LSClientCapabilities;
import org.eclipse.lsp4j.DocumentFormattingParams;

/**
 * Represents the formatting operation context.
 *
 * @since 2201.7.0
 */
public interface FormattingContext extends DocumentServiceContext {

    /**
     * Get document formatting params.
     *
     * @return {@link DocumentFormattingParams} Document formatting params.
     */
    DocumentFormattingParams getParams();

    /**
     * Get formatting options.
     *
     * @return {@link FormattingOptions} Formatting options.
     */
    FormattingOptions formattingOptions();

    /**
     * Get client capabilities.
     *
     * @return {@link LSClientCapabilities} Client capabilities.
     */
    LSClientCapabilities clientCapabilities();
}
