<script>
import Polaroid from './Polaroid.vue'
import DownloadablePolaroid from "./DownloadablePolaroid.vue";

export default {
  name: 'ImageGallery',
  components: {Polaroid, DownloadablePolaroid},
  data() {
    return {
      images: [],
      loading: true,
      error: null
    }
  },
  created() {
    fetch('/api/v1/images')
        .then(response => {
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          return response.json();
        })
        .then(imageData => {
          this.images = imageData;
          this.loading = false;
        })
        .catch((err) => {
          this.error = 'Failed to load images. Please try again later.';
          this.loading = false;
          if (process.env.NODE_ENV === 'development') {
            console.error(err);
          }
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
  <div>
    <v-progress-circular
        v-if="loading"
        indeterminate
        color="primary"
        class="loading-spinner"
    ></v-progress-circular>

    <v-alert
        v-else-if="error"
        type="error"
        variant="tonal"
        class="ma-4"
    >
      {{ error }}
    </v-alert>

    <div v-else class="d-flex flex-row flex-wrap align-center" :class="{ 'justify-center': isMobile }">
      <div v-for="(imageData) in images" :key="imageData.id">
        <polaroid v-if="isMobile" class="ma-2" :imageData="imageData"/>
        <downloadable-polaroid v-else class="ma-2" :imageData="imageData"/>
      </div>
    </div>
  </div>
</template>


<style scoped>
.loading-spinner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
