/*
 * This file is a shadowed version of the older javadoc codebase on which gosudoc is based; borrowed from jdk 9.
 */

package gw.gosudoc.com.sun.tools.doclets.formats.html;

import java.io.*;
import java.util.*;



import gw.gosudoc.com.sun.javadoc.ClassDoc;
import gw.gosudoc.com.sun.tools.doclets.internal.toolkit.Content;
import gw.gosudoc.com.sun.tools.doclets.internal.toolkit.SerializedFormWriter;
import gw.gosudoc.com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import gw.gosudoc.com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import gw.gosudoc.com.sun.tools.doclets.formats.html.markup.*;

/**
 * Generate the Serialized Form Information Page.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 * @author Atul M Dambalkar
 */
@Deprecated
public class SerializedFormWriterImpl extends SubWriterHolderWriter
    implements SerializedFormWriter
{

    List<gw.gosudoc.com.sun.javadoc.ClassDoc> visibleClasses;

    /**
     * HTML tree for main tag.
     */
    private HtmlTree mainTree = HtmlTree.MAIN();

    /**
     * @param configuration the configuration data for the doclet
     * @throws IOException
     * @throws DocletAbortException
     */
    public SerializedFormWriterImpl(ConfigurationImpl configuration)
            throws IOException {
        super(configuration, DocPaths.SERIALIZED_FORM);
        visibleClasses = Arrays.asList(configuration.root.classes());
    }

    /**
     * Get the given header.
     *
     * @param header the header to write
     * @return the body content tree
     */
    public Content getHeader( String header) {
        HtmlTree bodyTree = getBody(true, getWindowTitle(header));
        HtmlTree htmlTree = (configuration.allowTag( HtmlTag.HEADER))
                ? HtmlTree.HEADER()
                : bodyTree;
        addTop(htmlTree);
        addNavLinks(true, htmlTree);
        if (configuration.allowTag(HtmlTag.HEADER)) {
            bodyTree.addContent(htmlTree);
        }
        Content h1Content = new StringContent(header);
        Content heading = HtmlTree.HEADING( HtmlConstants.TITLE_HEADING, true,
                HtmlStyle.title, h1Content);
        Content div = HtmlTree.DIV(HtmlStyle.header, heading);
        if (configuration.allowTag(HtmlTag.MAIN)) {
            mainTree.addContent(div);
        } else {
            bodyTree.addContent(div);
        }
        return bodyTree;
    }

    /**
     * Get the serialized form summaries header.
     *
     * @return the serialized form summary header tree
     */
    public Content getSerializedSummariesHeader() {
        HtmlTree ul = new HtmlTree(HtmlTag.UL);
        ul.addStyle(HtmlStyle.blockList);
        return ul;
    }

    /**
     * Get the package serialized form header.
     *
     * @return the package serialized form header tree
     */
    public Content getPackageSerializedHeader() {
        HtmlTree htmlTree;
        if (configuration.allowTag(HtmlTag.SECTION)) {
            htmlTree = HtmlTree.SECTION();
        } else {
            htmlTree = new HtmlTree(HtmlTag.LI);
            htmlTree.addStyle(HtmlStyle.blockList);
        }
        return htmlTree;
    }

    /**
     * Get the given package header.
     *
     * @param packageName the package header to write
     * @return a content tree for the package header
     */
    public Content getPackageHeader(String packageName) {
        Content heading = HtmlTree.HEADING(HtmlConstants.PACKAGE_HEADING, true,
                packageLabel);
        heading.addContent(getSpace());
        heading.addContent(packageName);
        return heading;
    }

    /**
     * Get the serialized class header.
     *
     * @return a content tree for the serialized class header
     */
    public Content getClassSerializedHeader() {
        HtmlTree ul = new HtmlTree(HtmlTag.UL);
        ul.addStyle(HtmlStyle.blockList);
        return ul;
    }

    /**
     * Checks if a class is generated and is visible.
     *
     * @param classDoc the class being processed.
     * @return true if the class, that is being processed, is generated and is visible.
     */
    public boolean isVisibleClass( gw.gosudoc.com.sun.javadoc.ClassDoc classDoc) {
        return visibleClasses.contains(classDoc) && configuration.isGeneratedDoc(classDoc);
    }

    /**
     * Get the serializable class heading.
     *
     * @param classDoc the class being processed
     * @return a content tree for the class header
     */
    public Content getClassHeader( gw.gosudoc.com.sun.javadoc.ClassDoc classDoc) {
        Content classLink = (isVisibleClass(classDoc)) ?
            getLink(new LinkInfoImpl(configuration, LinkInfoImpl.Kind.DEFAULT, classDoc)
            .label(configuration.getClassName(classDoc))) :
            new StringContent(classDoc.qualifiedName());
        Content li = HtmlTree.LI(HtmlStyle.blockList, getMarkerAnchor(
                classDoc.qualifiedName()));
        Content superClassLink =
            classDoc.superclassType() != null ?
                getLink(new LinkInfoImpl(configuration,
                        LinkInfoImpl.Kind.SERIALIZED_FORM,
                        classDoc.superclassType())) :
                null;

        //Print the heading.
        Content className = superClassLink == null ?
            configuration.getResource(
            "doclet.Class_0_implements_serializable", classLink) :
            configuration.getResource(
            "doclet.Class_0_extends_implements_serializable", classLink,
            superClassLink);
        li.addContent(HtmlTree.HEADING(HtmlConstants.SERIALIZED_MEMBER_HEADING,
                className));
        return li;
    }

    /**
     * Get the serial UID info header.
     *
     * @return a content tree for the serial uid info header
     */
    public Content getSerialUIDInfoHeader() {
        HtmlTree dl = new HtmlTree(HtmlTag.DL);
        dl.addStyle(HtmlStyle.nameValue);
        return dl;
    }

    /**
     * Adds the serial UID info.
     *
     * @param header the header that will show up before the UID.
     * @param serialUID the serial UID to print.
     * @param serialUidTree the serial UID content tree to which the serial UID
     *                      content will be added
     */
    public void addSerialUIDInfo(String header, String serialUID,
            Content serialUidTree) {
        Content headerContent = new StringContent(header);
        serialUidTree.addContent(HtmlTree.DT(headerContent));
        Content serialContent = new StringContent(serialUID);
        serialUidTree.addContent(HtmlTree.DD(serialContent));
    }

    /**
     * Get the class serialize content header.
     *
     * @return a content tree for the class serialize content header
     */
    public Content getClassContentHeader() {
        HtmlTree ul = new HtmlTree(HtmlTag.UL);
        ul.addStyle(HtmlStyle.blockList);
        return ul;
    }

    /**
     * Get the serialized content tree section.
     *
     * @param serializedTreeContent the serialized content tree to be added
     * @return a div content tree
     */
    public Content getSerializedContent(Content serializedTreeContent) {
        HtmlTree divContent = HtmlTree.DIV(HtmlStyle.serializedFormContainer,
                serializedTreeContent);
        if (configuration.allowTag(HtmlTag.MAIN)) {
            mainTree.addContent(divContent);
            return mainTree;
        } else {
            return divContent;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addPackageSerializedTree(Content serializedSummariesTree,
            Content packageSerializedTree) {
        serializedSummariesTree.addContent((configuration.allowTag(HtmlTag.SECTION))
                ? HtmlTree.LI(HtmlStyle.blockList, packageSerializedTree)
                : packageSerializedTree);
    }

    /**
     * Add the footer.
     *
     * @param serializedTree the serialized tree to be added
     */
    public void addFooter(Content serializedTree) {
        Content htmlTree = (configuration.allowTag(HtmlTag.FOOTER))
                ? HtmlTree.FOOTER()
                : serializedTree;
        addNavLinks(false, htmlTree);
        addBottom(htmlTree);
        if (configuration.allowTag(HtmlTag.FOOTER)) {
            serializedTree.addContent(htmlTree);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void printDocument(Content serializedTree) throws IOException {
        printHtmlDocument(null, true, serializedTree);
    }

    /**
     * Return an instance of a SerialFieldWriter.
     *
     * @return an instance of a SerialFieldWriter.
     */
    public SerialFieldWriter getSerialFieldWriter( gw.gosudoc.com.sun.javadoc.ClassDoc classDoc) {
        return new HtmlSerialFieldWriter(this, classDoc);
    }

    /**
     * Return an instance of a SerialMethodWriter.
     *
     * @return an instance of a SerialMethodWriter.
     */
    public SerialMethodWriter getSerialMethodWriter( ClassDoc classDoc) {
        return new HtmlSerialMethodWriter(this, classDoc);
    }
}