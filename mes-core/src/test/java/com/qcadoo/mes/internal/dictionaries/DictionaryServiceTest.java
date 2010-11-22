/**
 * ********************************************************************
 * Code developed by amazing QCADOO developers team.
 * Copyright (c) Qcadoo Limited sp. z o.o. (2010)
 * ********************************************************************
 */

package com.qcadoo.mes.internal.dictionaries;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.mes.api.DictionaryService;
import com.qcadoo.mes.beans.dictionaries.DictionariesDictionary;
import com.qcadoo.mes.beans.dictionaries.DictionariesDictionaryItem;
import com.qcadoo.mes.internal.DictionaryServiceImpl;

public class DictionaryServiceTest {

    private final SessionFactory sessionFactory = mock(SessionFactory.class, RETURNS_DEEP_STUBS);

    private DictionaryService dictionaryService = null;

    @Before
    public void init() {
        dictionaryService = new DictionaryServiceImpl();
        ReflectionTestUtils.setField(dictionaryService, "sessionFactory", sessionFactory);
    }

    @Test
    public void shouldReturnListOfDictionaries() throws Exception {
        // given
        DictionariesDictionary dict1 = new DictionariesDictionary();
        dict1.setName("Dict1");
        DictionariesDictionary dict2 = new DictionariesDictionary();
        dict2.setName("Dict2");
        DictionariesDictionary dict3 = new DictionariesDictionary();
        dict3.setName("Dict3");

        given(sessionFactory.getCurrentSession().createQuery("from Dictionary").list()).willReturn(
                newArrayList(dict1, dict2, dict3));

        // when
        Set<String> dictionaries = dictionaryService.dictionaries();

        // then
        assertThat(dictionaries.size(), equalTo(3));
        assertThat(dictionaries, hasItems("Dict1", "Dict2", "Dict3"));
    }

    @Test
    public void shouldReturnSortedListOfDictionaryValues() throws Exception {
        // given
        DictionariesDictionaryItem item1 = new DictionariesDictionaryItem();
        item1.setName("aaa");
        DictionariesDictionaryItem item2 = new DictionariesDictionaryItem();
        item2.setName("ccc");
        DictionariesDictionaryItem item3 = new DictionariesDictionaryItem();
        item3.setName("bbb");

        given(
                sessionFactory.getCurrentSession().createCriteria(DictionariesDictionaryItem.class)
                        .createAlias("dictionary", "dc").add(Mockito.any(Criterion.class)).add(Mockito.any(Criterion.class))
                        .addOrder(Mockito.any(Order.class)).list()).willReturn(newArrayList(item1, item3, item2));

        // when
        List<String> values = dictionaryService.values("dict");

        // then
        assertThat(values.size(), equalTo(3));
        assertThat(values.get(0), equalTo("aaa"));
        assertThat(values.get(1), equalTo("bbb"));
        assertThat(values.get(2), equalTo("ccc"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrownAnExceptionIfDictionaryNameIsNull() throws Exception {
        // when
        dictionaryService.values(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrownAnExceptionIfDictionaryNameIsEmpty() throws Exception {
        // when
        dictionaryService.values("");
    }

}
