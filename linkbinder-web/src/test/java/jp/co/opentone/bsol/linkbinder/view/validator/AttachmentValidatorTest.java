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
package jp.co.opentone.bsol.linkbinder.view.validator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.render.Renderer;

import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;



/**
 * {@link AttachmentValidator}のテストケース.
 * @author opentone
 */
public class AttachmentValidatorTest extends AbstractTestCase {

    /* メッセージ取得キー */
    /**
     * 最大桁数を越えた場合のメッセージID.
     */
    public static final String MSG_ID_MAXIMUM = "length_maximum";

    private AttachmentValidator validator;

    private FacesContext context;
    private UIComponent component;

    @Before
    public void setUp() {
        validator = new AttachmentValidator();
        context = new FacesContextMock();
        component = createUIComponentMock();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateIllegalArgument() {
        validator.validate(null, component, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateIllegalArgumentr2() {
        validator.validate(context, null, null);
    }

    /**
     * Validatorが生成したメッセージが期待された状態であるか検証する.
     * @param m Validatorが生成したメッセージ
     * @param key 格納を期待するメッセージキー
     */
    private void assertValidatorMessage(FacesMessage m, String key) {
        assertNotNull(m);
        assertEquals(FacesMessage.SEVERITY_ERROR, m.getSeverity());

        Message expected = Messages.getMessage(key);
        assertEquals(expected.getSummary(), m.getSummary());

        // 変数部を置換した上で検証
        String expectedMessage = expected.getMessage();
        expectedMessage = expectedMessage.replaceAll("\\{0\\}", "100");

        assertEquals(expectedMessage, m.getDetail());
    }

    // TODO 確認後削除
//    private static class MockUploadedFile implements UploadedFile {
//
//        String name;
//        public void setName(String name) {
//            this.name = name;
//        }
//        /* (non-Javadoc)
//         * @see org.apache.myfaces.custom.fileupload.UploadedFile#getBytes()
//         */
//        public byte[] getBytes() throws IOException {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.myfaces.custom.fileupload.UploadedFile#getInputStream()
//         */
//        public InputStream getInputStream() throws IOException {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.myfaces.custom.fileupload.UploadedFile#getContentType()
//         */
//        public String getContentType() {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.myfaces.custom.fileupload.UploadedFile#getName()
//         */
//        public String getName() {
//            return name;
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.myfaces.custom.fileupload.UploadedFile#getSize()
//         */
//        public long getSize() {
//            // TODO Auto-generated method stub
//            return 0;
//        }
//
//        /* (non-Javadoc)
//         * @see org.apache.myfaces.custom.fileupload.UploadedFile#getStorageStrategy()
//         */
//        public StorageStrategy getStorageStrategy() {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//    }

    private static UIComponent createUIComponentMock() {
        return new UIComponent() {
            @Override
            protected void addFacesListener(FacesListener listener) {
            }

            @Override
            public void broadcast(FacesEvent event) throws AbortProcessingException {
            }

            @Override
            public void decode(FacesContext context) {
            }

            @Override
            public void encodeBegin(FacesContext context) throws IOException {
            }

            @Override
            public void encodeChildren(FacesContext context) throws IOException {
            }

            @Override
            public void encodeEnd(FacesContext context) throws IOException {
            }

            @Override
            public UIComponent findComponent(String expr) {
                return null;
            }

            @Override
            public Map getAttributes() {
                return null;
            }

            @Override
            public int getChildCount() {
                return 0;
            }

            @Override
            public List getChildren() {
                return null;
            }

            @Override
            public String getClientId(FacesContext context) {
                return null;
            }

            @Override
            protected FacesContext getFacesContext() {
                return null;
            }

            @Override
            protected FacesListener[] getFacesListeners(Class clazz) {
                return null;
            }

            @Override
            public UIComponent getFacet(String name) {
                return null;
            }

            @Override
            public Map getFacets() {
                return null;
            }

            @Override
            public Iterator getFacetsAndChildren() {
                return null;
            }

            @Override
            public String getFamily() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public UIComponent getParent() {
                return null;
            }

            @Override
            protected Renderer getRenderer(FacesContext context) {
                return null;
            }

            @Override
            public String getRendererType() {
                return null;
            }

            @Override
            public boolean getRendersChildren() {
                return false;
            }

            @Override
            public ValueBinding getValueBinding(String name) {
                return null;
            }

            @Override
            public boolean isRendered() {
                return false;
            }

            @Override
            public void processDecodes(FacesContext context) {
            }

            @Override
            public void processRestoreState(FacesContext context, Object state) {
            }

            @Override
            public Object processSaveState(FacesContext context) {
                return null;
            }

            @Override
            public void processUpdates(FacesContext context) {
            }

            @Override
            public void processValidators(FacesContext context) {
            }

            @Override
            public void queueEvent(FacesEvent event) {
            }

            @Override
            protected void removeFacesListener(FacesListener listener) {
            }

            @Override
            public void setId(String id) {
            }

            @Override
            public void setParent(UIComponent parent) {
            }

            @Override
            public void setRendered(boolean rendered) {
            }

            @Override
            public void setRendererType(String rendererType) {
            }

            @Override
            public void setValueBinding(String name, ValueBinding binding) {
            }

            public boolean isTransient() {
                return false;
            }

            public void restoreState(FacesContext context, Object state) {
            }

            public Object saveState(FacesContext context) {
                return null;
            }

            public void setTransient(boolean newTransientValue) {
            }

        };
    }
}
