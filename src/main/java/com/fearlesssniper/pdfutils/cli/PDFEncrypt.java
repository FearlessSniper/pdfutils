/*
 * The MIT License
 *
 * Copyright 2021 fearlesssniper.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.fearlesssniper.pdfutils.cli;

import com.fearlesssniper.pdfutils.cli.common.PDFParameter;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Encrypts the PDF with a password
 * @author fearlesssniper
 */
@Command(
    name = "encrypt",
    description = "Encrypts a PDF with a password or" +
    " add access permissions to the document."
)
public class PDFEncrypt implements Callable<Integer> {
    
    @Option(
        names = "--prefer-aes",
        description = {
            "Whether AES is prefered when several"
            + " encryption methods are available.",
            "This setting is only relevant when the key length is 128 bits."
        },
        defaultValue = "true"
    )
    private boolean preferAES;
    
    @Option(
        names = "--key-length",
        description = {
            "The length of the encryption key.",
            "Allows 40, 128, 256 bits.",
            "Default: ${DEFAULT-VALUE}"
        },
        defaultValue = "128"
    )
    private int encryptionKeyLength;

    // Password option
    @Option(
        names = {"--user-pass", "--user-password"},
        description = {
            "The user password.",
            "This password is used to encrypt the PDF"
        },
        interactive = true,
        required = true,
        defaultValue = "",
        arity = "0..1"
    )
    private String userPassword;
    
    // Set permissions
    private enum AccessPermissions {
        PRINT(AccessPermission::setCanPrint),
        PRINT_DEGRADED(AccessPermission::setCanPrintDegraded),
        MODIFY(AccessPermission::setCanModify),
        MODIFY_ANNOTATIONS(AccessPermission::setCanModifyAnnotations),
        ASSEMBLE(AccessPermission::setCanAssembleDocument),
        EXTRACT(AccessPermission::setCanExtractContent),
        EXTRACT_ACCESSIBILITY(AccessPermission::setCanExtractForAccessibility),
        FILL_FORM(AccessPermission::setCanFillInForm);
        
        private final BiConsumer<AccessPermission, Boolean> setter;
        private AccessPermissions(BiConsumer<AccessPermission, Boolean> setter) {
             this.setter = setter;
        }
        
        public BiConsumer<AccessPermission, Boolean> getSetter() {
            return this.setter;
        }
    }

    private static class PermissionOptions {
        @Option(
            names = {"--owner-password"},
            description = {
                "The owner password.",
                "This password is used to set the permissions."
            },
            interactive = true,
            defaultValue = "",
            arity = "0..1"
        )
        public String ownerPassword;
        
        @Option(
            names = {"-n", "--negate"},
            description = "Apply all permissions but the ones provided.",
            required = false
        )
        public boolean negate;

        @Option(
            names = {"--permissions"},
            description = "Access permission of the document.",
            arity = "0..*"
        )
        public AccessPermissions[] permissions;
    }

    @ArgGroup (exclusive = false, multiplicity = "0..1")
    private PermissionOptions permissionOptions;
    
    // Output file
    @Option(
        names = {"-o", "--output"},
        description = {"The output PDF document."}
    )
    private File outputFile;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private PDFParameter pdfArgs;

    @Override
    public Integer call() throws Exception {
        try (var pdfDoc = PDDocument.load(pdfArgs.docFile, pdfArgs.docPass)) {
            var accessPermission = new AccessPermission();
            if (this.permissionOptions != null) {
                if (!this.permissionOptions.negate) {
                    // Set everything to false if we only choose what to allow
                    for (var options: PDFEncrypt.AccessPermissions.values()) {
                        options.getSetter().accept(accessPermission, false);
                    }
                }
                for (var permission: permissionOptions.permissions) {
                    permission.getSetter().accept(accessPermission, !this.permissionOptions.negate);
                }
            }
            // Owner password group not provided: set ownerpass empty
            var ownerPass = 
                    this.permissionOptions == null? "":
                    this.permissionOptions.ownerPassword;
            var encryptPolicy = new StandardProtectionPolicy(
                    ownerPass,
                    this.userPassword,
                    accessPermission
            );
            encryptPolicy.setPreferAES(this.preferAES);
            encryptPolicy.setEncryptionKeyLength(this.encryptionKeyLength);
            
            pdfDoc.protect(encryptPolicy);
            var outFile = new File(
                FilenameUtils.removeExtension(this.pdfArgs.docFile.getName())
                + "_encrypted.pdf");
            pdfDoc.save(this.outputFile != null? outputFile: outFile);
        }
        return 0;
    }
    
    public static void main(String[] args) {
        var commandLine = new CommandLine(new PDFEncrypt());
//        commandLine.usage(System.out);
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }
}
