/**
 * Category â†’ subcategory â†’ tag taxonomy for /explore-topics.
 *
 * Transcribed verbatim from `prototyping/explore/explore-topics-fullpage.png`.
 * Column order in that screenshot is row-major across three columns:
 *   Life Â· Self Improvement Â· Work
 *   Technology Â· Software Development Â· Media
 *   Society Â· Culture Â· World
 * which is preserved in EXPLORE_TAXONOMY below.
 *
 * Every category carries exactly three subcategories. Work's third (Leadership) was
 * missing from the first transcription â€” it only became visible in
 * `prototyping/explore/taxonomy-grid layring.png`, where the devtools overlay shows the
 * full Work column: Business, Marketing, Leadership.
 */

export interface ExploreTag {
  readonly label: string;
  readonly slug: string;
}

export interface ExploreSubcategory {
  readonly label: string;
  readonly slug: string;
  /** The tags visible in the reference screenshot, in order. */
  readonly tags: readonly ExploreTag[];
  /**
   * SYNTHESIZED â€” not in the screenshot.
   *
   * The reference truncates each subcategory and hides the tail behind "More", so the
   * hidden portion is not visible anywhere in the image. These are plausible additions
   * written to give "More" something real to reveal. Replace with API data.
   */
  readonly hiddenTags: readonly ExploreTag[];
}

export interface ExploreCategory {
  readonly label: string;
  readonly slug: string;
  readonly subcategories: readonly ExploreSubcategory[];
}

export const EXPLORE_TAXONOMY: readonly ExploreCategory[] = [
  // â”€â”€ Row 1 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  {
    label: 'Life',
    slug: 'life',
    subcategories: [
      {
        label: 'Family',
        slug: 'life/family',
        tags: [
          { label: 'Adoption', slug: 'adoption' },
          { label: 'Children', slug: 'children' },
          { label: 'Elder Care', slug: 'elder-care' },
          { label: 'Fatherhood', slug: 'fatherhood' },
          { label: 'Motherhood', slug: 'motherhood' },
        ],
        hiddenTags: [
          { label: 'Parenting', slug: 'parenting' },
          { label: 'Siblinghood', slug: 'siblinghood' },
          { label: 'Newborns', slug: 'newborns' },
        ],
      },
      {
        label: 'Health',
        slug: 'life/health',
        tags: [
          { label: 'Aging', slug: 'aging' },
          { label: 'Coronavirus', slug: 'coronavirus' },
          { label: 'Covid-19', slug: 'covid-19' },
          { label: 'Death And Dying', slug: 'death-and-dying' },
          { label: 'Disease', slug: 'disease' },
        ],
        hiddenTags: [
          { label: 'Mental Health', slug: 'mental-health' },
          { label: 'Nutrition', slug: 'nutrition' },
          { label: 'Public Health', slug: 'public-health' },
          { label: 'Sleep', slug: 'sleep' },
        ],
      },
      {
        label: 'Relationships',
        slug: 'life/relationships',
        tags: [
          { label: 'Dating', slug: 'dating' },
          { label: 'Divorce', slug: 'divorce' },
          { label: 'Friendship', slug: 'friendship' },
          { label: 'Love', slug: 'love' },
          { label: 'Marriage', slug: 'marriage' },
        ],
        hiddenTags: [
          { label: 'Breakups', slug: 'breakups' },
          { label: 'Long Distance', slug: 'long-distance' },
          { label: 'Toxic Relationships', slug: 'toxic-relationships' },
        ],
      },
    ],
  },
  {
    label: 'Self Improvement',
    slug: 'self-improvement',
    subcategories: [
      {
        label: 'Mental Health',
        slug: 'self-improvement/mental-health',
        tags: [
          { label: 'Anxiety', slug: 'anxiety' },
          { label: 'Counseling', slug: 'counseling' },
          { label: 'Grief', slug: 'grief' },
          { label: 'Life Lessons', slug: 'life-lessons' },
          { label: 'Self-awareness', slug: 'self-awareness' },
        ],
        hiddenTags: [
          { label: 'Burnout', slug: 'burnout' },
          { label: 'Depression', slug: 'depression' },
          { label: 'Therapy', slug: 'therapy' },
        ],
      },
      {
        label: 'Productivity',
        slug: 'self-improvement/productivity',
        tags: [
          { label: 'Career Advice', slug: 'career-advice' },
          { label: 'Coaching', slug: 'coaching' },
          { label: 'Goal Setting', slug: 'goal-setting' },
          { label: 'Morning Routines', slug: 'morning-routines' },
          { label: 'Pomodoro Technique', slug: 'pomodoro-technique' },
        ],
        hiddenTags: [
          { label: 'Deep Work', slug: 'deep-work' },
          { label: 'Inbox Zero', slug: 'inbox-zero' },
          { label: 'Time Management', slug: 'time-management' },
          { label: 'Workflow', slug: 'workflow' },
        ],
      },
      {
        label: 'Mindfulness',
        slug: 'self-improvement/mindfulness',
        tags: [
          { label: 'Guided Meditation', slug: 'guided-meditation' },
          { label: 'Journaling', slug: 'journaling' },
          { label: 'Meditation', slug: 'meditation' },
          { label: 'Transcendental Meditation', slug: 'transcendental-meditation' },
          { label: 'Yoga', slug: 'yoga' },
        ],
        hiddenTags: [
          { label: 'Breathwork', slug: 'breathwork' },
          { label: 'Gratitude', slug: 'gratitude' },
          { label: 'Mindfulness', slug: 'mindfulness' },
        ],
      },
    ],
  },
  {
    label: 'Work',
    slug: 'work',
    subcategories: [
      {
        label: 'Business',
        slug: 'work/business',
        tags: [
          { label: 'Entrepreneurship', slug: 'entrepreneurship' },
          { label: 'Freelancing', slug: 'freelancing' },
          { label: 'Small Business', slug: 'small-business' },
          { label: 'Startups', slug: 'startups' },
          { label: 'Venture Capital', slug: 'venture-capital' },
        ],
        hiddenTags: [
          { label: 'E-Commerce', slug: 'e-commerce' },
          { label: 'Management', slug: 'management' },
          { label: 'Remote Work', slug: 'remote-work' },
        ],
      },
      {
        label: 'Marketing',
        slug: 'work/marketing',
        tags: [
          { label: 'Advertising', slug: 'advertising' },
          { label: 'Branding', slug: 'branding' },
          { label: 'Content Marketing', slug: 'content-marketing' },
          { label: 'Content Strategy', slug: 'content-strategy' },
          { label: 'Digital Marketing', slug: 'digital-marketing' },
        ],
        hiddenTags: [
          { label: 'Copywriting', slug: 'copywriting' },
          { label: 'Email Marketing', slug: 'email-marketing' },
          { label: 'Growth Hacking', slug: 'growth-hacking' },
          { label: 'SEO', slug: 'seo' },
          { label: 'Social Media', slug: 'social-media' },
        ],
      },
      {
        label: 'Leadership',
        slug: 'work/leadership',
        tags: [
          { label: 'Employee Engagement', slug: 'employee-engagement' },
          { label: 'Leadership Coaching', slug: 'leadership-coaching' },
          { label: 'Leadership Development', slug: 'leadership-development' },
          { label: 'Management', slug: 'management' },
          { label: 'Meetings', slug: 'meetings' },
        ],
        hiddenTags: [
          { label: 'Change Management', slug: 'change-management' },
          { label: 'Executive Presence', slug: 'executive-presence' },
          { label: 'Mentorship', slug: 'mentorship' },
        ],
      },
    ],
  },

  // â”€â”€ Row 2 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  {
    label: 'Technology',
    slug: 'technology',
    subcategories: [
      {
        label: 'Artificial Intelligence',
        slug: 'technology/artificial-intelligence',
        tags: [
          { label: 'ChatGPT', slug: 'chatgpt' },
          { label: 'Conversational AI', slug: 'conversational-ai' },
          { label: 'Deep Learning', slug: 'deep-learning' },
          { label: 'Large Language Models', slug: 'large-language-models' },
          { label: 'Machine Learning', slug: 'machine-learning' },
        ],
        hiddenTags: [
          { label: 'Generative AI', slug: 'generative-ai' },
          { label: 'Neural Networks', slug: 'neural-networks' },
          { label: 'NLP', slug: 'nlp' },
          { label: 'Prompt Engineering', slug: 'prompt-engineering' },
        ],
      },
      {
        label: 'Blockchain',
        slug: 'technology/blockchain',
        tags: [
          { label: 'Bitcoin', slug: 'bitcoin' },
          { label: 'Cryptocurrency', slug: 'cryptocurrency' },
          { label: 'Decentralized Finance', slug: 'decentralized-finance' },
          { label: 'Ethereum', slug: 'ethereum' },
          { label: 'NFT', slug: 'nft' },
        ],
        hiddenTags: [
          { label: 'Crypto Trading', slug: 'crypto-trading' },
          { label: 'Smart Contracts', slug: 'smart-contracts' },
          { label: 'Web3', slug: 'web3' },
        ],
      },
      {
        label: 'Data Science',
        slug: 'technology/data-science',
        tags: [
          { label: 'Analytics', slug: 'analytics' },
          { label: 'Data Engineering', slug: 'data-engineering' },
          { label: 'Data Visualization', slug: 'data-visualization' },
          { label: 'Database Design', slug: 'database-design' },
          { label: 'Sql', slug: 'sql' },
        ],
        hiddenTags: [
          { label: 'Big Data', slug: 'big-data' },
          { label: 'Data Cleaning', slug: 'data-cleaning' },
          { label: 'Machine Learning', slug: 'machine-learning' },
          { label: 'Pandas', slug: 'pandas' },
        ],
      },
    ],
  },
  {
    label: 'Software Development',
    slug: 'software-development',
    subcategories: [
      {
        label: 'Programming',
        slug: 'software-development/programming',
        tags: [
          { label: 'Android Development', slug: 'android-development' },
          { label: 'Coding', slug: 'coding' },
          { label: 'Flutter', slug: 'flutter' },
          { label: 'Frontend Engineering', slug: 'frontend-engineering' },
          { label: 'iOS Development', slug: 'ios-development' },
        ],
        hiddenTags: [
          { label: 'Backend Engineering', slug: 'backend-engineering' },
          { label: 'Full Stack', slug: 'full-stack' },
          { label: 'Mobile Development', slug: 'mobile-development' },
          { label: 'Testing', slug: 'testing' },
        ],
      },
      {
        label: 'Programming Languages',
        slug: 'software-development/programming-languages',
        tags: [
          { label: 'Angular', slug: 'angular' },
          { label: 'CSS', slug: 'css' },
          { label: 'HTML', slug: 'html' },
          { label: 'Java', slug: 'java' },
          { label: 'JavaScript', slug: 'javascript' },
        ],
        hiddenTags: [
          { label: 'Go', slug: 'go' },
          { label: 'Python', slug: 'python' },
          { label: 'Rust', slug: 'rust' },
          { label: 'TypeScript', slug: 'typescript' },
        ],
      },
      {
        label: 'Dev Ops',
        slug: 'software-development/dev-ops',
        tags: [
          { label: 'AWS', slug: 'aws' },
          { label: 'Databricks', slug: 'databricks' },
          { label: 'Docker', slug: 'docker' },
          { label: 'Kubernetes', slug: 'kubernetes' },
          { label: 'Terraform', slug: 'terraform' },
        ],
        hiddenTags: [
          { label: 'CI/CD', slug: 'ci-cd' },
          { label: 'Grafana', slug: 'grafana' },
          { label: 'Linux', slug: 'linux' },
          { label: 'Observability', slug: 'observability' },
        ],
      },
    ],
  },
  {
    label: 'Media',
    slug: 'media',
    subcategories: [
      {
        label: 'Writing',
        slug: 'media/writing',
        tags: [
          { label: '30 Day Challenge', slug: '30-day-challenge' },
          { label: 'Book Reviews', slug: 'book-reviews' },
          { label: 'Books', slug: 'books' },
          { label: 'Creative Nonfiction', slug: 'creative-nonfiction' },
          { label: 'Diary', slug: 'diary' },
        ],
        hiddenTags: [
          { label: 'Essays', slug: 'essays' },
          { label: 'Fiction', slug: 'fiction' },
          { label: 'Poetry', slug: 'poetry' },
          { label: 'Screenwriting', slug: 'screenwriting' },
        ],
      },
      {
        label: 'Art',
        slug: 'media/art',
        tags: [
          { label: 'Comics', slug: 'comics' },
          { label: 'Contemporary Art', slug: 'contemporary-art' },
          { label: 'Drawing', slug: 'drawing' },
          { label: 'Fine Art', slug: 'fine-art' },
          { label: 'Generative Art', slug: 'generative-art' },
        ],
        hiddenTags: [
          { label: 'Animation', slug: 'animation' },
          { label: 'Illustration', slug: 'illustration' },
          { label: 'Photography', slug: 'photography' },
          { label: 'Sculpture', slug: 'sculpture' },
        ],
      },
      {
        label: 'Gaming',
        slug: 'media/gaming',
        tags: [
          { label: 'Game Design', slug: 'game-design' },
          { label: 'Game Development', slug: 'game-development' },
          { label: 'Indie Game', slug: 'indie-game' },
          { label: 'Metaverse', slug: 'metaverse' },
          { label: 'Nintendo', slug: 'nintendo' },
        ],
        hiddenTags: [
          { label: 'Esports', slug: 'esports' },
          { label: 'Game Engines', slug: 'game-engines' },
          { label: 'Mobile Games', slug: 'mobile-games' },
          { label: 'Unity', slug: 'unity' },
        ],
      },
    ],
  },

  // â”€â”€ Row 3 â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  {
    label: 'Society',
    slug: 'society',
    subcategories: [
      {
        label: 'Economics',
        slug: 'society/economics',
        tags: [
          { label: 'Basic Income', slug: 'basic-income' },
          { label: 'Debt', slug: 'debt' },
          { label: 'Economy', slug: 'economy' },
          { label: 'Inflation', slug: 'inflation' },
          { label: 'Stock Market', slug: 'stock-market' },
        ],
        hiddenTags: [
          { label: 'Crypto Markets', slug: 'crypto-markets' },
          { label: 'Investing', slug: 'investing' },
          { label: 'Personal Finance', slug: 'personal-finance' },
        ],
      },
      {
        label: 'Education',
        slug: 'society/education',
        tags: [
          { label: 'Charter Schools', slug: 'charter-schools' },
          { label: 'Education Reform', slug: 'education-reform' },
          { label: 'Higher Education', slug: 'higher-education' },
          { label: 'PhD', slug: 'phd' },
          { label: 'Public Schools', slug: 'public-schools' },
        ],
        hiddenTags: [
          { label: 'Homeschooling', slug: 'homeschooling' },
          { label: 'Online Learning', slug: 'online-learning' },
          { label: 'Teaching', slug: 'teaching' },
        ],
      },
      {
        label: 'Equality',
        slug: 'society/equality',
        tags: [
          { label: 'Disability', slug: 'disability' },
          { label: 'Discrimination', slug: 'discrimination' },
          { label: 'Diversity In Tech', slug: 'diversity-in-tech' },
          { label: 'Feminism', slug: 'feminism' },
          { label: 'Inclusion', slug: 'inclusion' },
        ],
        hiddenTags: [
          { label: 'Accessibility', slug: 'accessibility' },
          { label: 'LGBTQ', slug: 'lgbtq' },
          { label: 'Racism', slug: 'racism' },
        ],
      },
    ],
  },
  {
    label: 'Culture',
    slug: 'culture',
    subcategories: [
      {
        label: 'Philosophy',
        slug: 'culture/philosophy',
        tags: [
          { label: 'Atheism', slug: 'atheism' },
          { label: 'Epistemology', slug: 'epistemology' },
          { label: 'Ethics', slug: 'ethics' },
          { label: 'Existentialism', slug: 'existentialism' },
          { label: 'Metaphysics', slug: 'metaphysics' },
        ],
        hiddenTags: [
          { label: 'Logic', slug: 'logic' },
          { label: 'Political Philosophy', slug: 'political-philosophy' },
          { label: 'Stoicism', slug: 'stoicism' },
        ],
      },
      {
        label: 'Religion',
        slug: 'culture/religion',
        tags: [
          { label: 'Buddhism', slug: 'buddhism' },
          { label: 'Christianity', slug: 'christianity' },
          { label: 'Hinduism', slug: 'hinduism' },
          { label: 'Islam', slug: 'islam' },
          { label: 'Judaism', slug: 'judaism' },
        ],
        hiddenTags: [
          { label: 'Christianity', slug: 'christianity' },
          { label: 'Sikhism', slug: 'sikhism' },
          { label: 'Taoism', slug: 'taoism' },
        ],
      },
      {
        label: 'Spirituality',
        slug: 'culture/spirituality',
        tags: [
          { label: 'Astrology', slug: 'astrology' },
          { label: 'Energy Healing', slug: 'energy-healing' },
          { label: 'Horoscopes', slug: 'horoscopes' },
          { label: 'Mysticism', slug: 'mysticism' },
          { label: 'Reiki', slug: 'reiki' },
        ],
        hiddenTags: [
          { label: 'Chakras', slug: 'chakras' },
          { label: 'Crystals', slug: 'crystals' },
          { label: 'Tarot', slug: 'tarot' },
        ],
      },
    ],
  },
  {
    label: 'World',
    slug: 'world',
    subcategories: [
      {
        label: 'Cities',
        slug: 'world/cities',
        tags: [
          { label: 'Abu Dhabi', slug: 'abu-dhabi' },
          { label: 'Amsterdam', slug: 'amsterdam' },
          { label: 'Athens', slug: 'athens' },
          { label: 'Bangkok', slug: 'bangkok' },
          { label: 'Barcelona', slug: 'barcelona' },
        ],
        hiddenTags: [
          { label: 'Berlin', slug: 'berlin' },
          { label: 'London', slug: 'london' },
          { label: 'Tokyo', slug: 'tokyo' },
        ],
      },
      {
        label: 'Nature',
        slug: 'world/nature',
        tags: [
          { label: 'Birding', slug: 'birding' },
          { label: 'Camping', slug: 'camping' },
          { label: 'Climate Change', slug: 'climate-change' },
          { label: 'Conservation', slug: 'conservation' },
          { label: 'Hiking', slug: 'hiking' },
        ],
        hiddenTags: [
          { label: 'Climate Science', slug: 'climate-science' },
          { label: 'Ecology', slug: 'ecology' },
          { label: 'Renewable Energy', slug: 'renewable-energy' },
          { label: 'Wildlife', slug: 'wildlife' },
        ],
      },
      {
        label: 'Travel',
        slug: 'world/travel',
        tags: [
          { label: 'Tourism', slug: 'tourism' },
          { label: 'Travel Tips', slug: 'travel-tips' },
          { label: 'Travel Writing', slug: 'travel-writing' },
          { label: 'Vacation', slug: 'vacation' },
          { label: 'Vanlife', slug: 'vanlife' },
        ],
        hiddenTags: [
          { label: 'Backpacking', slug: 'backpacking' },
          { label: 'Budget Travel', slug: 'budget-travel' },
          { label: 'Food', slug: 'food' },
        ],
      },
    ],
  },
];
