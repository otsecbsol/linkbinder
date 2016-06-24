/*
 * Copyright 2016 OPEN TONE Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.opentone.bsol.framework.core.extension.rome.feed.synd;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;

import jp.co.opentone.bsol.framework.core.util.XMLUtil;

/**
 * @author opentone
 */
public class ExtendedSyndEntryImpl extends SyndEntryImpl implements SyndEntry {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 6134760486108143325L;

    private static String removeInvalidChars(String s) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        return XMLUtil.removeInvalidChars(s);
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getAuthor()
     */
    @Override
    public String getAuthor() {
        return removeInvalidChars(super.getAuthor());
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getAuthors()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List getAuthors() {
        List<SyndPerson> authors = super.getAuthors();
        List<SyndPerson> result = new ArrayList<SyndPerson>(authors.size());
        for (SyndPerson p : authors) {
            SyndPerson pp = new SyndPersonImpl();
            if (p != null) {
                pp.setEmail(removeInvalidChars(p.getEmail()));
                pp.setName(removeInvalidChars(p.getName()));
                pp.setModules(p.getModules());
                pp.setUri(removeInvalidChars(p.getUri()));
            }
            result.add(pp);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getCategories()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List getCategories() {
        List<SyndCategory> categories = super.getCategories();
        List<SyndCategory> result = new ArrayList<SyndCategory>(categories.size());
        for (SyndCategory c : categories) {
            SyndCategory cc = new SyndCategoryImpl();
            if (c != null) {
                cc.setName(removeInvalidChars(c.getName()));
                cc.setTaxonomyUri(removeInvalidChars(c.getTaxonomyUri()));
            }
            result.add(cc);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getContents()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List getContents() {
        List<SyndContent> contents = super.getContents();
        List<SyndContent> result = new ArrayList<SyndContent>(contents.size());
        for (SyndContent c : contents) {
            SyndContent cc = new SyndContentImpl();
            if (c != null) {
                cc.setType(removeInvalidChars(c.getType()));
                cc.setMode(removeInvalidChars(c.getMode()));
                cc.setValue(removeInvalidChars(c.getValue()));
            }
            result.add(cc);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getContributors()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List getContributors() {
        List<SyndPerson> contributors = super.getContributors();
        ArrayList<SyndPerson> result = new ArrayList<SyndPerson>();
        for (SyndPerson p : contributors) {
            SyndPersonImpl pp = new SyndPersonImpl();
            if (p != null) {
                pp.setEmail(removeInvalidChars(p.getEmail()));
                pp.setUri(removeInvalidChars(p.getUri()));
                pp.setName(removeInvalidChars(p.getName()));
                pp.setModules(p.getModules());
            }
            result.add(pp);

        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getDescription()
     */
    @Override
    public SyndContent getDescription() {
        SyndContent c = super.getDescription();
        SyndContent result = new SyndContentImpl();
        if (c != null) {
            result.setType(c.getType());
            result.setMode(c.getMode());
            result.setValue(removeInvalidChars(c.getValue()));
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getEnclosures()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List getEnclosures() {
        List<SyndEnclosure> enc = super.getEnclosures();
        List<SyndEnclosure> result = new ArrayList<SyndEnclosure>(enc.size());
        for (SyndEnclosure e : enc) {
            SyndEnclosureImpl ee = new SyndEnclosureImpl();
            if (e != null) {
                ee.setLength(e.getLength());
                ee.setType(removeInvalidChars(e.getType()));
                ee.setUrl(removeInvalidChars(e.getUrl()));
            }
            result.add(ee);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getLink()
     */
    @Override
    public String getLink() {
        return removeInvalidChars(super.getLink());
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getLinks()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List getLinks() {
        List<SyndLink> links = super.getLinks();
        List<SyndLink> result = new ArrayList<SyndLink>(links.size());
        for (SyndLink l : links) {
            SyndLinkImpl ll = new SyndLinkImpl();
            if (l != null) {
                ll.setHref(removeInvalidChars(l.getHref()));
                ll.setHreflang(removeInvalidChars(l.getHreflang()));
                ll.setLength(l.getLength());
                ll.setRel(removeInvalidChars(l.getRel()));
                ll.setTitle(removeInvalidChars(l.getTitle()));
                ll.setType(removeInvalidChars(l.getType()));
            }
            result.add(ll);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getTitle()
     */
    @Override
    public String getTitle() {
        return removeInvalidChars(super.getTitle());
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getTitleEx()
     */
    @Override
    public SyndContent getTitleEx() {
        SyndContent c = super.getTitleEx();
        SyndContentImpl result = new SyndContentImpl();
        if (c != null) {
            result.setMode(removeInvalidChars(c.getMode()));
            result.setType(removeInvalidChars(c.getType()));
            result.setValue(removeInvalidChars(c.getValue()));
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.synd.SyndEntry#getUri()
     */
    @Override
    public String getUri() {
        return removeInvalidChars(super.getUri());
    }
}
