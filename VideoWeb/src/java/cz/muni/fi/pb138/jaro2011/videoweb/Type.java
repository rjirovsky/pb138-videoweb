package cz.muni.fi.pb138.jaro2011.videoweb;

/**
 *
 * @author Honza
 */
public enum Type {
    ORIGINAL
    {
        public String toSTring()
        {
            return "originál";
        }
    }, 
    MAGAZINE
    {
        public String toString()
        {
            return "magazín";
        }
    }, 
    HOME
    {
        public String toString()
        {
            return "domácí";
        }
    }, 
    COPY
    {
        public String toString()
        {
            return "kopie";
        }
    }
}

