<script>
import Polaroid from './Polaroid.vue'
import DownloadablePolaroid from "./DownloadablePolaroid.vue";

export default {
  components: {Polaroid, DownloadablePolaroid},
  data() {
    return {
      images: []
    }
  },
  created() {
    fetch('/api/v1/images')
        .then(response => response.json())
        .then(imageData => this.images = imageData)
        .catch((err) => {
          console.error(err);
        })
  },
  computed: {
    isMobile() {
      return this.$vuetify.display.mobile;
    }
  }
}
</script>

<template>
  <div class="d-flex flex-row flex-wrap align-center" :class="{ 'justify-center': isMobile } ">
    <div v-for="(imageData) in images" :key="imageData.id">
      <polaroid v-if="isMobile" class="ma-2" :imageData="imageData"/>
      <downloadable-polaroid v-if="!isMobile" class="ma-2" :imageData="imageData"/>
    </div>
  </div>
</template>


<style scoped>
</style>
