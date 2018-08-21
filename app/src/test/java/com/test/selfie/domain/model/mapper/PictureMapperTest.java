package com.test.selfie.domain.model.mapper;

import com.test.selfie.data.entity.PictureEntity;
import com.test.selfie.domain.model.Picture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class PictureMapperTest {

    @Mock
    private PictureEntity mEntity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // I used WHEN here because this class PictureEntity doesn't have setters
        when(mEntity.getId()).thenReturn("id");
        when(mEntity.getTitle()).thenReturn("title");
        when(mEntity.getThumbnail()).thenReturn("thumbnail");
        when(mEntity.getUrl()).thenReturn("url");
    }

    @Test
    public void transform() {
        Picture modal = PictureMapper.transform(mEntity);
        assertValues(modal);
    }

    @Test
    public void transformList() {
        List<PictureEntity> entities = new ArrayList<>();
        entities.add(mEntity);

        List<Picture> modals = PictureMapper.transform(entities);
        for (Picture modal : modals) {
            assertValues(modal);
        }
    }

    private void assertValues(Picture model) {
        assertThat(model.getId(), is(mEntity.getId()));
        assertThat(model.getTitle(), is(mEntity.getTitle()));
        assertThat(model.getThumbnail(), is(mEntity.getThumbnail()));
        assertThat(model.getUrl(), is(mEntity.getUrl()));
    }
}