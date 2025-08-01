package com.jeevision.cibseven.migrator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ProcessVersionTest {

    @Test
    void fromString_should_parse_major_minor_and_patch_versions_from_string_correctly(){
        ProcessVersion processVersion = ProcessVersion.fromString("1.3.5").get();

        assertThat(processVersion.getMajorVersion()).isEqualTo(1);
        assertThat(processVersion.getMinorVersion()).isEqualTo(3);
        assertThat(processVersion.getPatchVersion()).isEqualTo(5);
    }

    @Test
    void fromString_should_return_empty_optional_if_string_is_null(){
    	assertThat(ProcessVersion.fromString(null).isPresent()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.3,0", "1.3.", "1.3.0.2", "1..3", ".1.3", "13..", "1.3..4", "a.1.3", "1.b.c", "1.2.%"})
    void fromString_should_return_empty_optional_if_version_tag_is_faulty(String versionTagString) {
    	assertThat(ProcessVersion.fromString(versionTagString).isPresent()).isFalse();
    }

    @Test
    void toVersionTag_should_create_string_from_major_minor_and_patch_version() {
        assertThat(new ProcessVersion(2,3,5).toVersionTag()).isEqualTo("2.3.5");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.3.5", "12.3.0", "1.144.1", "0.0.0", "1522.1.44422", "142.142.142"})
    void fromString_toVersionTag_should_result_in_original_String_for_valid_version_tags(String versionTagString){
        assertThat(ProcessVersion.fromString(versionTagString).get().toVersionTag()).isEqualTo(versionTagString);
    }

    @Test
    void isOlderVersionThan_should_return_true_for_lower_major_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderVersionThan(new ProcessVersion(3,1,2))).isTrue();
    }

    @Test
    void isOlderVersionThan_should_return_true_for_lower_minor_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderVersionThan(new ProcessVersion(2,4,2))).isTrue();
    }

    @Test
    void isOlderVersionThan_should_return_true_for_lower_patch_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderVersionThan(new ProcessVersion(2,3,7))).isTrue();
    }

    @Test
    void isOlderVersionThan_should_return_false_for_higher_major_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderVersionThan(new ProcessVersion(1,4,7))).isFalse();
    }

    @Test
    void isOlderVersionThan_should_return_false_for_higher_minor_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderVersionThan(new ProcessVersion(2,2,7))).isFalse();
    }

    @Test
    void isOlderVersionThan_should_return_false_for_higher_patch_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderVersionThan(new ProcessVersion(2,3,4))).isFalse();
    }

    @Test
    void isOlderVersionThat_should_return_false_for_equal_versions() {
        assertThat(new ProcessVersion(2,3,5).isOlderVersionThan(new ProcessVersion(2,3,5))).isFalse();
    }

    @Test
    void isOlderPatchThan_should_return_false_for_lower_major_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderPatchThan(new ProcessVersion(3,1,2))).isFalse();
    }

    @Test
    void isOlderPatchThan_should_return_false_for_lower_minor_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderPatchThan(new ProcessVersion(2,4,2))).isFalse();
    }

    @Test
    void isOlderPatchThan_should_return_true_for_lower_patch_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderPatchThan(new ProcessVersion(2,3,7))).isTrue();
    }

    @Test
    void isOlderPatchThan_should_return_false_for_higher_major_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderPatchThan(new ProcessVersion(1,4,7))).isFalse();
    }

    @Test
    void isOlderPatchThan_should_return_false_for_higher_minor_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderPatchThan(new ProcessVersion(2,2,7))).isFalse();
    }

    @Test
    void isOlderPatchThan_should_return_false_for_higher_patch_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderPatchThan(new ProcessVersion(2,3,4))).isFalse();
    }

    @Test
    void isOlderPatchThan_should_return_false_for_equal_versions() {
        assertThat(new ProcessVersion(2,3,5).isOlderPatchThan(new ProcessVersion(2,3,5))).isFalse();
    }

    @Test
    void isOlderMinorThan_should_return_false_for_lower_major_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMinorThan(new ProcessVersion(3,1,2))).isFalse();
    }

    @Test
    void isOlderMinorThan_should_return_true_for_lower_minor_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMinorThan(new ProcessVersion(2,4,2))).isTrue();
    }

    @Test
    void isOlderMinorThan_should_return_false_for_lower_patch_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMinorThan(new ProcessVersion(2,3,7))).isFalse();
    }

    @Test
    void isOlderMinorThan_should_return_false_for_higher_major_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMinorThan(new ProcessVersion(1,4,7))).isFalse();
    }

    @Test
    void isOlderMinorThan_should_return_false_for_higher_minor_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMinorThan(new ProcessVersion(2,2,7))).isFalse();
    }

    @Test
    void isOlderMinorThan_should_return_false_for_higher_patch_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMinorThan(new ProcessVersion(2,3,4))).isFalse();
    }

    @Test
    void isOlderMinorThan_should_return_false_for_equal_versions() {
        assertThat(new ProcessVersion(2,3,5).isOlderMinorThan(new ProcessVersion(2,3,5))).isFalse();
    }

    @Test
    void isOlderMajorThan_should_return_true_for_lower_major_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMajorThan(new ProcessVersion(3,1,2))).isTrue();
    }

    @Test
    void isOlderMajorThan_should_return_false_for_lower_minor_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMajorThan(new ProcessVersion(2,4,2))).isFalse();
    }

    @Test
    void isOlderMajorThan_should_return_false_for_lower_patch_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMajorThan(new ProcessVersion(2,3,7))).isFalse();
    }

    @Test
    void isOlderMajorThan_should_return_false_for_higher_major_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMajorThan(new ProcessVersion(1,4,7))).isFalse();
    }

    @Test
    void isOlderMajorThan_should_return_false_for_higher_minor_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMajorThan(new ProcessVersion(2,2,7))).isFalse();
    }

    @Test
    void isOlderMajorThan_should_return_false_for_higher_patch_version() {
        assertThat(new ProcessVersion(2,3,5).isOlderMajorThan(new ProcessVersion(2,3,4))).isFalse();
    }

    @Test
    void isOlderMajorThan_should_return_false_for_equal_versions() {
        assertThat(new ProcessVersion(2,3,5).isOlderMajorThan(new ProcessVersion(2,3,5))).isFalse();
    }
}
