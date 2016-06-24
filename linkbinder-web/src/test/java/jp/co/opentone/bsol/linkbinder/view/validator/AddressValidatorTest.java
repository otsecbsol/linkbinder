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
import java.lang.reflect.Type;
import java.util.ArrayList;
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
import javax.faces.validator.ValidatorException;

import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.User;
import net.arnx.jsonic.JSON;



/**
 * {@link AddressValidator}のテストケース.
 * @author opentone
 */
public class AddressValidatorTest extends AbstractTestCase {

    /**
     * JSON形式の宛先をデコードするための型情報.
     */
    private static final Type MAPPING_TYPE;
    static {
        try {
            MAPPING_TYPE =
                    AddressValidatorTest.class.getDeclaredField("m").getGenericType();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /* メッセージ取得キー */
    /**
     * 形式不正.
     */
    private static final String KEY_ADDRESS_FORMAT = "address_format";
    /**
     * 宛先未入力エラー.
     */
    private static final String KEY_ADDRESS_REQUIRED = "address_required";
    /**
     * Attention未入力エラー.
     */
    private static final String KEY_ADDRESS_ATTENTION_REQUIRED = "address_attention_required";

    /**
     *
     * JSON形式の宛先をデコードするための型情報を取得するためのダミーフィールド.
     * staticイニシャライザ内で名前を参照されるだけで、処理には一切使用されない.
     */
    @SuppressWarnings("unused")
    private List<AddressCorresponGroup> m;

    private AddressValidator toValidator;
    private AddressValidator ccValidator;

    private FacesContext context;
    private UIComponent component;

    @Before
    public void setUp() {
        toValidator = new AddressValidator(MAPPING_TYPE, true);
        ccValidator = new AddressValidator(MAPPING_TYPE, false);
        context = new FacesContextMock();
        component = createUIComponentMock();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateIllegalArgument() {
        toValidator.validate(null, component, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateIllegalArgumentr2() {
        toValidator.validate(context, null, null);
    }

    /**
     * 検証対象データの形式が不正な場合の検証.
     * @throws Exception
     */
    @Test
    public void testValidateInvalidFormat() throws Exception {
        String value = "[aa"; //    不正なJSON形式文字列
        try {
            toValidator.validate(context, component, value);
            fail("Toは未入力だと例外が発生すべき");
        } catch (ValidatorException actual) {
            FacesMessage m = actual.getFacesMessage();
            assertValidatorMessage(m, KEY_ADDRESS_FORMAT);
        }
    }

    /**
     * Toが空の場合の検証
     * @throws Exception
     */
    @Test
    public void testValidateToAddressGroupEmpty() throws Exception {
        List<AddressCorresponGroup> to = new ArrayList<AddressCorresponGroup>();
        try {
            toValidator.validate(context, component, toJSON(to));
            fail("Toは未入力だと例外が発生すべき");
        } catch (ValidatorException actual) {
            FacesMessage m = actual.getFacesMessage();
            assertValidatorMessage(m, KEY_ADDRESS_REQUIRED);
        }
    }

    /**
     * ToのAttention未設定の場合の検証
     * @throws Exception
     */
    @Test
    public void testValidateToAttentionEmpty() throws Exception {
        List<AddressCorresponGroup> to = createAddresses();
        //  宛先-ユーザーを空にして検証
        AddressCorresponGroup ag = to.get(to.size() - 1);
        ag.setUsers(new ArrayList<AddressUser>());

        try {
            toValidator.validate(context, component, toJSON(to));
            fail("Toは、Attention未入力だと例外が発生すべき");
        } catch (ValidatorException actual) {
            FacesMessage m = actual.getFacesMessage();
            assertValidatorMessage(m, KEY_ADDRESS_ATTENTION_REQUIRED);
        }
    }

    /**
     * Toが削除済の場合の検証
     * @throws Exception
     */
    @Test
    public void testValidateToAddressGroupRemoved() throws Exception {
        List<AddressCorresponGroup> to = createAddresses();
        UpdateMode.setUpdateMode(to, UpdateMode.DELETE);
        try {
            toValidator.validate(context, component, toJSON(to));
            fail("Toは、未入力だと例外が発生すべき");
        } catch (ValidatorException actual) {
            FacesMessage m = actual.getFacesMessage();
            assertValidatorMessage(m, KEY_ADDRESS_REQUIRED);
        }
    }

    /**
     * Ccが空の場合の検証.
     * 空でも例外が発生しないはず.
     * @throws Exception
     */
    @Test
    public void testValidateCcAddressGroupEmpty() throws Exception {
        List<AddressCorresponGroup> cc = new ArrayList<AddressCorresponGroup>();
        ccValidator.validate(context, component, toJSON(cc));
        assertTrue(true);
    }

    /**
     * CcのAttention未設定の場合の検証
     * 空でも例外が発生しないはず.
     * @throws Exception
     */
    @Test
    public void testValidateCcUserEmpty() throws Exception {
        List<AddressCorresponGroup> cc = createAddresses();
        //  宛先-ユーザーを空にして検証
        AddressCorresponGroup ag = cc.get(cc.size() - 1);
        ag.setUsers(new ArrayList<AddressUser>());

        ccValidator.validate(context, component, toJSON(cc));
        assertTrue(true);
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
        assertEquals(expected.getMessage(), m.getDetail());
    }

    private static List<AddressCorresponGroup> createAddresses() {
        List<AddressCorresponGroup> addresses = new ArrayList<AddressCorresponGroup>();
        AddressCorresponGroup ag;
        CorresponGroup g;
        List<AddressUser> users;
        AddressUser au;
        User u;

        ag = new AddressCorresponGroup();
        g = new CorresponGroup();
        g.setId(1L);
        g.setName("foo");
        ag.setCorresponGroup(g);

        users = new ArrayList<AddressUser>();
        au = new AddressUser();
        u = new User();
        u.setEmpNo("00001");
        u.setNameE("name1");
        au.setUser(u);
        users.add(au);

        ag.setUsers(users);

        addresses.add(ag);

        return addresses;
    }

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
    private static String toJSON(List<AddressCorresponGroup> addresses) throws Exception {
        return JSON.encode(addresses);
    }
}
